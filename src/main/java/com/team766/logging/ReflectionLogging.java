package com.team766.logging;

import com.google.common.reflect.TypeToken;
import com.team766.library.ReflectionUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;

public class ReflectionLogging {
    private interface ObjectLogger {
        void recordOutputImpl(Object value, String key) throws Exception;

        void toLogImpl(Object value, String key, LogTable table) throws Exception;

        Object fromLogImpl(String key, LogTable table) throws Exception;
    }

    private record LoggerMapKey(Type fieldType, Class<?> objectType) {}

    private static final Map<LoggerMapKey, ObjectLogger> loggers = new HashMap<>();

    private static ObjectLogger getLogger(Type fieldType, Class<?> objectType) throws Exception {
        var key = new LoggerMapKey(fieldType, objectType);
        var logger = loggers.get(key);
        if (logger == null) {
            logger = makeLogger(fieldType, objectType);
            loggers.put(key, logger);
        }
        return logger;
    }

    @SuppressWarnings("unchecked")
    private static ObjectLogger makeLogger(Type fieldType, Class<?> objectClass) throws Exception {
        Class<?> fieldClass = ReflectionUtils.getRawType(fieldType);

        try {
            var recordMethod =
                    ReflectionUtils.searchForMethod(
                            Logger.class, "recordOutput", String.class, objectClass);
            var putMethod =
                    ReflectionUtils.searchForMethod(
                            LogTable.class, "put", String.class, objectClass);
            var getMethod =
                    ReflectionUtils.searchForMethod(
                            LogTable.class, "get", String.class, objectClass);
            Object defaultValue;
            try {
                defaultValue =
                        fieldClass.isArray()
                                ? Array.newInstance(fieldClass.getComponentType(), 0)
                                : ReflectionUtils.isPrimitive(fieldClass)
                                        ? Array.get(
                                                Array.newInstance(
                                                        ReflectionUtils.translateToPrimitive(
                                                                fieldClass),
                                                        1),
                                                0)
                                        : fieldClass.getConstructor().newInstance();
            } catch (NoSuchMethodError ex) {
                throw new UnsupportedOperationException(
                        "WPILib type "
                                + fieldClass
                                + " doesn't have a default constructor. Special support will need to be added.",
                        ex);
            }
            return new ObjectLogger() {
                @Override
                public void recordOutputImpl(Object value, String key) throws Exception {
                    recordMethod.invoke(null, key, value);
                }

                @Override
                public void toLogImpl(Object value, String key, LogTable table) throws Exception {
                    putMethod.invoke(table, key, value);
                }

                @Override
                public Object fromLogImpl(String key, LogTable table) throws Exception {
                    return getMethod.invoke(table, key, defaultValue);
                }
            };
        } catch (NoSuchMethodException ex) {
        }

        if (objectClass.isRecord()) {
            Constructor<?> constructor;
            constructor =
                    objectClass.getConstructor(
                            Arrays.stream(objectClass.getRecordComponents())
                                    .map(c -> c.getType())
                                    .toArray(Class<?>[]::new));
            var fields = objectClass.getRecordComponents();
            return new ObjectLogger() {
                @Override
                public void recordOutputImpl(Object value, String key) throws Exception {
                    key += '/';
                    for (var field : fields) {
                        Object fieldValue = field.getAccessor().invoke(value);
                        recordOutput(fieldValue, key + field.getName());
                    }
                }

                @Override
                public void toLogImpl(Object value, String key, LogTable table) throws Exception {
                    var subtable = table.getSubtable(key);
                    for (var field : fields) {
                        Object fieldValue = field.getAccessor().invoke(value);
                        toLog(fieldValue, field.getGenericType(), field.getName(), subtable);
                    }
                }

                @Override
                public Object fromLogImpl(String key, LogTable table) throws Exception {
                    var subtable = table.getSubtable(key);
                    var args = new Object[fields.length];
                    for (int i = 0; i < fields.length; ++i) {
                        var field = fields[i];
                        args[i] = fromLog(field.getGenericType(), field.getName(), subtable);
                    }
                    return constructor.newInstance(args);
                }
            };
        }

        if (objectClass.isArray()) {
            Class<?> elementType = objectClass.getComponentType();
            return new ObjectLogger() {
                @Override
                public void recordOutputImpl(Object array, String key) throws Exception {
                    int length = Array.getLength(array);
                    for (int i = 0; i < length; ++i) {
                        recordOutput(Array.get(array, i), key + '/' + i);
                    }
                }

                @Override
                public void toLogImpl(Object array, String key, LogTable table) throws Exception {
                    var subtable = table.getSubtable(key);
                    int length = Array.getLength(array);
                    subtable.put(".length", length);
                    for (int i = 0; i < length; ++i) {
                        toLog(Array.get(array, i), elementType, Integer.toString(i), subtable);
                    }
                }

                @Override
                public Object fromLogImpl(String key, LogTable table) throws Exception {
                    var subtable = table.getSubtable(key);
                    int length = subtable.get(".length", 0);
                    var array = Array.newInstance(elementType, length);
                    for (int i = 0; i < length; ++i) {
                        Array.set(array, i, fromLog(elementType, Integer.toString(i), subtable));
                    }
                    return array;
                }
            };
        }

        if (Collection.class.isAssignableFrom(objectClass)) {
            Class<?> elementType;
            if (Collection.class.isAssignableFrom(fieldClass)) {
                var collectionType =
                        ((TypeToken<? extends Collection<?>>) TypeToken.of(fieldType))
                                .getSupertype(Collection.class);
                elementType =
                        ReflectionUtils.getRawType(
                                ReflectionUtils.getTypeArguments(collectionType)[0]);
            } else {
                elementType = null;
            }
            Function<Object[], Collection<Object>> constructor;
            if (Collection.class.isAssignableFrom(fieldClass)
                    && !Modifier.isAbstract(fieldClass.getModifiers())) {
                Constructor<?> ctor = fieldClass.getConstructor(Collection.class);
                constructor =
                        array -> {
                            try {
                                return (Collection<Object>) ctor.newInstance(Arrays.asList(array));
                            } catch (InstantiationException
                                    | IllegalAccessException
                                    | InvocationTargetException ex) {
                                throw ReflectionUtils.sneakyThrow(ex);
                            }
                        };
            } else if (fieldClass.isAssignableFrom(List.class)) {
                constructor = Arrays::asList;
            } else if (fieldClass.isAssignableFrom(Set.class)) {
                constructor = Set::of;
            } else {
                throw new IllegalArgumentException("Unsupported collection type " + fieldClass);
            }
            return new ObjectLogger() {
                @Override
                public void recordOutputImpl(Object value, String key) throws Exception {
                    Collection<?> collection = (Collection<?>) value;
                    int i = 0;
                    for (var element : collection) {
                        recordOutput(element, key + '/' + i);
                        ++i;
                    }
                }

                @Override
                public void toLogImpl(Object value, String key, LogTable table) throws Exception {
                    Collection<?> collection = (Collection<?>) value;
                    var subtable = table.getSubtable(key);
                    int length = collection.size();
                    subtable.put(".length", length);
                    int i = 0;
                    for (var element : collection) {
                        toLog(element, elementType, Integer.toString(i), subtable);
                        ++i;
                    }
                }

                @Override
                public Object fromLogImpl(String key, LogTable table) throws Exception {
                    var subtable = table.getSubtable(key);
                    int length = subtable.get(".length", 0);
                    var array = Array.newInstance(elementType, length);
                    for (int i = 0; i < length; ++i) {
                        Array.set(array, i, fromLog(elementType, Integer.toString(i), subtable));
                    }
                    return constructor.apply((Object[]) array);
                }
            };
        }

        if (Map.class.isAssignableFrom(objectClass)) {
            Class<?> keyType;
            Class<?> valueType;
            if (Map.class.isAssignableFrom(fieldClass)) {
                var mapType =
                        ((TypeToken<? extends Map<?, ?>>) TypeToken.of(fieldType))
                                .getSupertype(Map.class);
                keyType = ReflectionUtils.getRawType(ReflectionUtils.getTypeArguments(mapType)[0]);
                valueType =
                        ReflectionUtils.getRawType(ReflectionUtils.getTypeArguments(mapType)[1]);
            } else {
                keyType = null;
                valueType = null;
            }

            Supplier<Map<Object, Object>> constructor;
            if (!Modifier.isAbstract(fieldClass.getModifiers())) {
                Constructor<?> ctor = fieldClass.getConstructor();
                constructor =
                        () -> {
                            try {
                                return (Map<Object, Object>) ctor.newInstance();
                            } catch (InstantiationException
                                    | IllegalAccessException
                                    | InvocationTargetException ex) {
                                throw ReflectionUtils.sneakyThrow(ex);
                            }
                        };
            } else if (fieldClass.isAssignableFrom(Map.class)) {
                constructor = HashMap::new;
            } else {
                throw new IllegalArgumentException("Unsupported map type " + fieldClass);
            }
            return new ObjectLogger() {
                @Override
                public void recordOutputImpl(Object value, String key) throws Exception {
                    var map = (Map<?, ?>) value;
                    int i = 0;
                    for (var entry : map.entrySet()) {
                        recordOutput(entry.getKey(), key + '/' + i + "/key");
                        recordOutput(entry.getValue(), key + '/' + i + "/value");
                        ++i;
                    }
                }

                @Override
                public void toLogImpl(Object value, String key, LogTable table) throws Exception {
                    var map = (Map<?, ?>) value;
                    var subtable = table.getSubtable(key);
                    subtable.put(".size", map.size());
                    int i = 0;
                    for (var entry : map.entrySet()) {
                        var entrySubtable = subtable.getSubtable(Integer.toString(i));
                        toLog(entry.getKey(), keyType, "key", entrySubtable);
                        toLog(entry.getValue(), valueType, "value", entrySubtable);
                        ++i;
                    }
                }

                @Override
                public Object fromLogImpl(String key, LogTable table) throws Exception {
                    Map<Object, Object> map = constructor.get();
                    var subtable = table.getSubtable(key);
                    int size = subtable.get(".size", 0);
                    for (int i = 0; i < size; ++i) {
                        var entrySubtable = subtable.getSubtable(Integer.toString(i));
                        map.put(
                                fromLog(keyType, "key", entrySubtable),
                                fromLog(valueType, "value", entrySubtable));
                    }
                    return map;
                }
            };
        }

        throw new IllegalArgumentException(objectClass + " is not serializable for logging");
    }

    private static String typeKey(String key) {
        return '.' + key + ".type";
    }

    public static void recordOutput(Object value, String key) throws Exception {
        if (value == null) {
            return;
        }
        getLogger(value.getClass(), value.getClass()).recordOutputImpl(value, key);
    }

    public static void toLog(Object value, Type fieldType, String key, LogTable table)
            throws Exception {
        if (value == null) {
            return;
        }
        var valueClass = value.getClass();
        if (fieldType == null
                || !valueClass.equals(
                        ReflectionUtils.translateFromPrimitive(
                                ReflectionUtils.getRawType(fieldType)))) {
            table.put(typeKey(key), valueClass.descriptorString());
        }
        if (fieldType == null) {
            fieldType = valueClass;
        }
        getLogger(fieldType, valueClass).toLogImpl(value, key, table);
    }

    public static Object fromLog(Type fieldType, String key, LogTable table) throws Exception {
        String loggedType = table.get(typeKey(key), "");
        Class<?> objectType;
        try {
            objectType = ReflectionUtils.resolveClassDescriptor(loggedType);
            if (fieldType == null) {
                fieldType = objectType;
            }
        } catch (Exception ex) {
            if (fieldType == null) {
                throw new IllegalArgumentException("Type argument is null and type was not logged");
            }
            objectType = ReflectionUtils.getRawType(fieldType);
        }
        return getLogger(fieldType, objectType).fromLogImpl(key, table);
    }
}

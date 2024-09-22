package com.team766.library;

import com.google.common.reflect.TypeToken;
import java.lang.constant.ClassDesc;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {
    public static Method searchForMethod(Class<?> clazz, String name, Class<?>... params)
            throws NoSuchMethodException {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ex) {
            for (Method method : clazz.getMethods()) {
                // Has to be named the same of course.
                if (!method.getName().equals(name)) {
                    continue;
                }

                Class<?>[] types = method.getParameterTypes();

                // Does it have the same number of arguments that we're looking for.
                if (types.length != params.length) {
                    continue;
                }

                // Check for type compatibility
                if (areTypesCompatible(types, params)) {
                    return method;
                }
            }
            throw ex;
        }
    }

    private static boolean areTypesCompatible(Class<?>[] targets, Class<?>[] sources) {
        if (targets.length != sources.length) {
            return false;
        }

        for (int i = 0; i < targets.length; i++) {
            if (sources[i] == null) {
                continue;
            }

            if (!translateFromPrimitive(targets[i]).isAssignableFrom(sources[i])) {
                return false;
            }
        }
        return (true);
    }

    private static Map<Class<?>, Class<?>> primitiveToBoxed = new HashMap<>();
    private static Map<Class<?>, Class<?>> boxedToPrimitive = new HashMap<>();

    static {
        primitiveToBoxed.put(Boolean.TYPE, Boolean.class);
        primitiveToBoxed.put(Character.TYPE, Character.class);
        primitiveToBoxed.put(Byte.TYPE, Byte.class);
        primitiveToBoxed.put(Short.TYPE, Short.class);
        primitiveToBoxed.put(Integer.TYPE, Integer.class);
        primitiveToBoxed.put(Long.TYPE, Long.class);
        primitiveToBoxed.put(Float.TYPE, Float.class);
        primitiveToBoxed.put(Double.TYPE, Double.class);
        for (var kvp : primitiveToBoxed.entrySet()) {
            boxedToPrimitive.put(kvp.getValue(), kvp.getKey());
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        return boxedToPrimitive.containsKey(clazz);
    }

    public static Class<?> translateToPrimitive(Class<?> primitive) {
        if (primitive.isPrimitive()) {
            return primitive;
        }
        return boxedToPrimitive.computeIfAbsent(
                primitive,
                p -> {
                    throw new IllegalArgumentException("Error translating type: " + p);
                });
    }

    public static Class<?> translateFromPrimitive(Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            return primitive;
        }
        return primitiveToBoxed.computeIfAbsent(
                primitive,
                p -> {
                    throw new IllegalArgumentException("Error translating type: " + p);
                });
    }

    public static Class<?> getRawType(Type type) {
        return TypeToken.of(type).getRawType();
    }

    public static Type[] getTypeArguments(TypeToken<?> typeToken) {
        return ((ParameterizedType) typeToken.getType()).getActualTypeArguments();
    }

    public static Class<?> resolveClassDescriptor(String classDesc)
            throws ReflectiveOperationException {
        if (classDesc == null || classDesc.isEmpty()) {
            throw new IllegalArgumentException("classDesc is empty");
        }
        return (Class<?>)
                ClassDesc.ofDescriptor(classDesc).resolveConstantDesc(MethodHandles.lookup());
    }

    @SuppressWarnings("unchecked")
    public static <E extends Throwable> E sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }
}

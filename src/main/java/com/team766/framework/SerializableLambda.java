package com.team766.framework;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public interface SerializableLambda extends Serializable {
    public interface Runnable extends java.lang.Runnable, SerializableLambda {}

    public interface Supplier<T> extends java.util.function.Supplier<T>, SerializableLambda {}

    private static SerializedLambda getSerializedLambda(SerializableLambda lambda) {
        SerializedLambda serializedLambda = null;
        for (Class<?> cl = lambda.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method method = cl.getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                Object replacement = method.invoke(lambda);
                if (!(replacement instanceof SerializedLambda)) {
                    break; // custom interface implementation
                }
                serializedLambda = (SerializedLambda) replacement;
                break;
            } catch (NoSuchMethodException e) {
                // ignore
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        if (serializedLambda == null) {
            throw new IllegalArgumentException("Cannot find SerializedLambda for " + lambda);
        }
        return serializedLambda;
    }

    /* package */ static Object[] getLambdaCaptures(SerializableLambda lambda) {
        final SerializedLambda serializedLambda = getSerializedLambda(lambda);
        final int count = serializedLambda.getCapturedArgCount();
        final Object[] result = new Object[count];
        for (int i = 0; i < count; ++i) {
            result[i] = serializedLambda.getCapturedArg(i);
        }
        return result;
    }
}

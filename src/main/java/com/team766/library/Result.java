package com.team766.library;

import java.lang.reflect.UndeclaredThrowableException;

public sealed interface Result<T, E extends Throwable> {
    final record Value<T, E extends Throwable>(T value) implements Result<T, E> {
        @Override
        public T get() throws E {
            return value;
        }
    }

    final record Exception<T, E extends Throwable>(E exception) implements Result<T, E> {
        @Override
        public T get() throws E {
            throw exception;
        }
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    static <T, E extends Throwable> Result<T, E> capture(
            ThrowingSupplier<T, E> supplier, E... typeHint) {
        final Class<?> exceptionType = typeHint.getClass().getComponentType();
        try {
            return new Value<T, E>(supplier.get());
        } catch (Throwable ex) {
            if (exceptionType.isInstance(ex)) {
                return new Exception<T, E>((E) ex);
            } else if (ex instanceof RuntimeException rex) {
                throw rex;
            } else if (ex instanceof Error err) {
                throw err;
            } else {
                throw new UndeclaredThrowableException(ex);
            }
        }
    }

    T get() throws E;

    default boolean hasValue() {
        return this instanceof Value;
    }

    default boolean hasException() {
        return this instanceof Exception;
    }
}

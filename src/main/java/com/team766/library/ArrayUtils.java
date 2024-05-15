package com.team766.library;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ArrayUtils {
    @SafeVarargs
    public static <E> E[] initializeArray(
            final int size, final IntFunction<E> elementFactory, E... elementTypeHint) {
        E[] array = Arrays.copyOf(elementTypeHint, size);
        Arrays.setAll(array, elementFactory);
        return array;
    }

    @SafeVarargs
    public static <E> E[] initializeArray(
            final int size, final Supplier<E> elementFactory, E... elementTypeHint) {
        return initializeArray(size, elementFactory, elementTypeHint);
    }
}

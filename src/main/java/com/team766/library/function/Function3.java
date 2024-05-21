package com.team766.library.function;

public interface Function3<A, B, C, R> extends FunctionBase {
    R apply(A a, B b, C c);
}

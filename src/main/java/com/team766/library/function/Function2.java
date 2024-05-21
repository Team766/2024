package com.team766.library.function;

public interface Function2<A, B, R> extends FunctionBase {
    R apply(A a, B b);
}

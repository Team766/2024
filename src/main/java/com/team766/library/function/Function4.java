package com.team766.library.function;

public interface Function4<A, B, C, D, R> extends FunctionBase {
    R apply(A a, B b, C c, D d);
}

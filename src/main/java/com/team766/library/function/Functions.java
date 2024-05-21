/** Generated from the template in Functions.java.template */
package com.team766.library.function;

public interface Functions {

    public interface Consumer1<Arg1> extends FunctionBase {
        void accept(Arg1 arg1);
    }

    public interface Consumer2<Arg1, Arg2> extends FunctionBase {
        void accept(Arg1 arg1, Arg2 arg2);
    }

    public interface Consumer3<Arg1, Arg2, Arg3> extends FunctionBase {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3);
    }

    public interface Consumer4<Arg1, Arg2, Arg3, Arg4> extends FunctionBase {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4);
    }

    public interface Consumer5<Arg1, Arg2, Arg3, Arg4, Arg5> extends FunctionBase {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5);
    }

    public interface Consumer6<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> extends FunctionBase {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6);
    }

    public interface Consumer7<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> extends FunctionBase {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6, Arg7 arg7);
    }

    public interface Consumer8<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8>
            extends FunctionBase {
        void accept(
                Arg1 arg1,
                Arg2 arg2,
                Arg3 arg3,
                Arg4 arg4,
                Arg5 arg5,
                Arg6 arg6,
                Arg7 arg7,
                Arg8 arg8);
    }

    public interface Function1<Arg1, R> extends FunctionBase {
        R apply(Arg1 arg1);
    }

    public interface Function2<Arg1, Arg2, R> extends FunctionBase {
        R apply(Arg1 arg1, Arg2 arg2);
    }

    public interface Function3<Arg1, Arg2, Arg3, R> extends FunctionBase {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3);
    }

    public interface Function4<Arg1, Arg2, Arg3, Arg4, R> extends FunctionBase {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4);
    }

    public interface Function5<Arg1, Arg2, Arg3, Arg4, Arg5, R> extends FunctionBase {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5);
    }

    public interface Function6<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, R> extends FunctionBase {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6);
    }

    public interface Function7<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, R> extends FunctionBase {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6, Arg7 arg7);
    }

    public interface Function8<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, R>
            extends FunctionBase {
        R apply(
                Arg1 arg1,
                Arg2 arg2,
                Arg3 arg3,
                Arg4 arg4,
                Arg5 arg5,
                Arg6 arg6,
                Arg7 arg7,
                Arg8 arg8);
    }
}

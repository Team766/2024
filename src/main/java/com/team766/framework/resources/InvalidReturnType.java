/** Generated from the template in InvalidReturnType.java.template */
package com.team766.framework.resources;

class InvalidReturnType {
    interface Runnable {
        void run();
    }

    interface Provider<R> {
        R get();
    }

    interface Consumer1<Arg1> {
        void accept(Arg1 arg1);
    }

    interface Consumer2<Arg1, Arg2> {
        void accept(Arg1 arg1, Arg2 arg2);
    }

    interface Consumer3<Arg1, Arg2, Arg3> {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3);
    }

    interface Consumer4<Arg1, Arg2, Arg3, Arg4> {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4);
    }

    interface Consumer5<Arg1, Arg2, Arg3, Arg4, Arg5> {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5);
    }

    interface Consumer6<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6);
    }

    interface Consumer7<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> {
        void accept(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6, Arg7 arg7);
    }

    interface Consumer8<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> {
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

    interface Function1<Arg1, R> {
        R apply(Arg1 arg1);
    }

    interface Function2<Arg1, Arg2, R> {
        R apply(Arg1 arg1, Arg2 arg2);
    }

    interface Function3<Arg1, Arg2, Arg3, R> {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3);
    }

    interface Function4<Arg1, Arg2, Arg3, Arg4, R> {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4);
    }

    interface Function5<Arg1, Arg2, Arg3, Arg4, Arg5, R> {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5);
    }

    interface Function6<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, R> {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6);
    }

    interface Function7<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, R> {
        R apply(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6, Arg7 arg7);
    }

    interface Function8<Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, R> {
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

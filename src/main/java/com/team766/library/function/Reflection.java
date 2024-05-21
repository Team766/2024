package com.team766.library.function;

import com.github.meanbeanlib.mirror.Executables;
import java.util.HashMap;
import java.util.Map;

public class Reflection {
    private static Map<Class<? extends FunctionBase>, Class<?>[]> lambdaParamsCache =
            new HashMap<>();

    public static Class<?>[] findLambdaParams(FunctionBase f) {
        return lambdaParamsCache.computeIfAbsent(
                f.getClass(), fc -> Executables.findExecutable(f).getParameterTypes());
    }
}

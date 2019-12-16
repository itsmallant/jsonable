package com.pt.jsonable;

import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @desc:
 * @author: ningqiang.zhao
 * @time: 2019-12-13 15:29
 **/
public class JsonKnife {
    private static final String TAG = "JsonKnife";
    private static boolean debug = true;
    private static final Map<Class<?>, Method> BINDINGS = new LinkedHashMap<>();

    public static JSONObject convert(Object obj) {
        JSONObject jsonObject = null;
        try {
            Method toJsonMethod = findToJsonMethod(obj);
            toJsonMethod.setAccessible(true);
            jsonObject = (JSONObject) toJsonMethod.invoke(null, obj);
        } catch (Exception e) {
            e.getMessage();
        }
        return jsonObject;
    }

    private static Method findToJsonMethod(Object obj) throws IllegalStateException, ClassNotFoundException {
        Class<?> cls = obj.getClass();
        Method toJsonMethod = BINDINGS.get(cls);
        if (toJsonMethod != null || BINDINGS.containsKey(cls)) {
            if (debug) {
                Log.d(TAG, "HIT: Cached in binding map.");
            }
            return toJsonMethod;
        }
        Package clsPkg = cls.getPackage();
        String pkgName = clsPkg == null ? "" : clsPkg.getName();
        Class<?> utilClass = cls.getClassLoader().loadClass(pkgName + ".JsonAbleUtil");
        Method[] methods;
        try {
            methods = utilClass.getDeclaredMethods();
        } catch (Throwable throwable) {
            methods = utilClass.getMethods();
        }
        for (Method method : methods) {
            if ("toJson".equals(method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    BINDINGS.put(parameterTypes[0], method);
                } else {
                    String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                    throw new RuntimeException(methodName + "toJson method must have exactly 1 parameter but has " + parameterTypes.length);
                }
            }
        }
        return BINDINGS.get(cls);
    }

}

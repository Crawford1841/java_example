package org.example.basic;

import com.sun.istack.internal.Nullable;
import org.example.contant.Constant;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class LogCtx {


    public static void adjustStep() {
        String step = MDC.get(Constant.STEP_ID);
        if (!isEmpty(step)) {
            MDC.put(Constant.STEP_ID, "" + (Integer.parseInt(step) + 1));
        }
    }

    public static boolean isEmpty(@Nullable Object str) {
        return (str == null || "".equals(str));
    }

    public static void target(Object target) {
        MDC.put(Constant.TARGET_ID, target.getClass().getSimpleName());
    }

    public static void rmTarget() {
        MDC.remove(Constant.TARGET_ID);
    }
}

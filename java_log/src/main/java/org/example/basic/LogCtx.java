package org.example.basic;

import com.sun.istack.internal.Nullable;
import org.example.contant.Constant;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class LogCtx {

    //private static final Logger log = LoggerFactory.getLogger(LogCtx.class);

    //public static final String SYSTEM = "system";
    //
    //private static Logger logger = LoggerFactory.getLogger(SYSTEM);

    //TODO: 暂时不需要做租户日志隔离--规模、资源达不到
    //private static Map<String, Logger> loggers = new HashMap<>(8);
    static {
        //Map<String, Tenant> tenants = TenantCache.getTenants();
        //log.info("租户缓存大小 {}  ", tenants.size());
        //for (Entry<String, Tenant> tenant : tenants.entrySet()) {
        //    String tenantId = tenant.getValue().getTenantId() + "";
        //    log.info("加载租户{}logger", tenantId);
        //
        //    loggers.put(tenantId, LoggerFactory.getLogger(tenantId));
        //}
        //
        //loggers.put(SYSTEM, LoggerFactory.getLogger(SYSTEM));
    }

    public static Logger logger() {
        //String tenantId = RequestContext.getTenantId();
        //log.info("获取租户{}logger", tenantId);
        //Logger logger = loggers.get(tenantId);
        //if (null == logger) {
        //    log.info("获取租户{}logger为空，使用默认logger", RequestContext.getTenantId());
        //    Logger logger = loggers.get(SYSTEM);
        //}

        //return logger;

        return null;
    }

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

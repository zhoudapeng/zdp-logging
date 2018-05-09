package com.zdp.logging.util;

import org.apache.http.client.methods.HttpPost;

import java.util.UUID;

/**
 * @author <a href="mailto:zhoudapeng8888@126.com">zhoudapeng</a>
 * Date 2018/5/9
 * Time 下午1:19
 */
public class TraceIdUtil {
    public static String getTraceId() {
        String traceId = MDCUtil.get(MDCUtil.Type.TRACE_ID);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }
}

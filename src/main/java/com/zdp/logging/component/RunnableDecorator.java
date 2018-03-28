package com.zdp.logging.component;

import com.zdp.logging.util.MDCUtil;

public class RunnableDecorator implements Runnable {
    private Runnable runnable;
    private String traceId;

    public RunnableDecorator(Runnable runnable) {
        this.runnable = runnable;
        String traceId = MDCUtil.get(MDCUtil.Type.TRACE_ID);
        if (traceId == null) {
            traceId = "";
        }
        this.traceId = traceId;
    }

    public void run() {
        MDCUtil.put(MDCUtil.Type.TRACE_ID,traceId);
        runnable.run();
        MDCUtil.clear();
    }
}

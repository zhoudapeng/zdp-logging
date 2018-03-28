package com.zdp.logging.component;

public class RunnableWrapper {
    public static Runnable wrap(Runnable runnable) {
        return new RunnableDecorator(runnable);
    }
}

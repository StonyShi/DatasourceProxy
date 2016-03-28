package com.stone.db.proxy.controller.context;

import javax.servlet.AsyncContext;
import java.util.concurrent.Callable;

/**
 * Created by Stony on 2016/3/14.
 */
public abstract class CanceledCallable implements Callable {
    public AsyncContext asyncContext;

    public CanceledCallable(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }
}

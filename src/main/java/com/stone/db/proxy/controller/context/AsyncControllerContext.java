package com.stone.db.proxy.controller.context;

import com.stone.db.proxy.utils.Jackson;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Stony on 2016/3/14.
 */
public class AsyncControllerContext implements InitializingBean{

    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    private BlockingQueue<Runnable>  queue;
    private boolean initializing = false;
    private final Object initMonitor = new Object();
    private ThreadPoolExecutor executor;
    private int corePoolSize = 10;
    private int maximumPoolSize = 30;
    private long keepAliveTimeInSeconds = 60L;
    private long asyncTimeoutInSeconds = 60L;
    private RejectedExecutionHandler executionHandler;
    private AsyncListener asyncListener;
    public static final String ATTRIBUTE_URI = "uri";
    public static final String ATTRIBUTE_PARAMS = "params";


    public void submit(final HttpServletRequest req, final Callable<Object> task){
        final String uri = req.getRequestURI();
        final Map<String, String[]> params = req.getParameterMap();
        final AsyncContext asyncContext = req.startAsync();
        asyncContext.getRequest().setAttribute(ATTRIBUTE_URI, uri);
        asyncContext.getRequest().setAttribute(ATTRIBUTE_PARAMS, params);
        asyncContext.setTimeout(asyncTimeoutInSeconds * 1000);
        if(asyncListener != null) {
            asyncContext.addListener(asyncListener);
        }
        executor.submit(new CanceledCallable(asyncContext) {
            @Override
            public Object call() throws Exception {
                Object result = task.call();
                if(result != null) {
                    callBack(asyncContext, result, uri, params);
                }else{
                    callBack(asyncContext, "", uri, params);
                }
                return null;
            }
        });
    }
    private void callBack(AsyncContext asyncContext, Object result, String uri, Map<String, String[]> params) {
        HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
        try {
            if(result instanceof String) {
                write(resp, (String)result);
            } else {
                write(resp, Jackson.toJSON(result));
            }
        } catch (Throwable e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //程序内部错误
            try {
                logger.error("get info error, uri : {},  params : {}", uri, Jackson.toJSON(params), e);
            } catch (Exception ex) {}
        } finally {
            asyncContext.complete();
        }
    }

    private void write(HttpServletResponse resp, String result) {
        OutputStream stream;
        try {
            resp.setHeader("Content-type","html/text;charset=UTF-8");
            stream = resp.getOutputStream();
            stream.write(result.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!initializing){
            queue = new ArrayBlockingQueue<Runnable>(186);
            executor = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTimeInSeconds,
                    TimeUnit.SECONDS,
                    queue);
            if(this.executionHandler == null){
                this.executionHandler = new DefaultAsyncControllerRejectedExecutionHandler();
            }
            if(this.asyncListener == null){
                this.asyncListener = new DefaultAsyncListener();
            }
            executor.setRejectedExecutionHandler(executionHandler);
            synchronized (initMonitor){
                this.initializing = true;
            }
        }

    }
    private void asyncContextAfter(AsyncContext asyncContext,AsyncContextStatus status){
        if(asyncContext != null) {
            try {
                String uri = (String) asyncContext.getRequest().getAttribute(ATTRIBUTE_URI);
                Map params = (Map) asyncContext.getRequest().getAttribute(ATTRIBUTE_PARAMS);
                logger.error("async request {}, uri : {}, params : {}", status, uri, Jackson.toJSON(params));
            } catch (Exception e) {
                logger.error("async request {}, error ",status, e);
            }
            try {
                HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                asyncContext.complete();
            }
        }
    }

    public void setQueue(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setKeepAliveTimeInSeconds(long keepAliveTimeInSeconds) {
        this.keepAliveTimeInSeconds = keepAliveTimeInSeconds;
    }

    public void setExecutionHandler(RejectedExecutionHandler executionHandler) {
        this.executionHandler = executionHandler;
    }

    public void setAsyncListener(AsyncListener asyncListener) {
        this.asyncListener = asyncListener;
    }



    class DefaultAsyncListener implements AsyncListener{

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            AsyncContext asyncContext = event.getAsyncContext();
            logger.debug("complete Async uri : {}", asyncContext.getRequest().getAttribute(ATTRIBUTE_URI));
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            AsyncContext asyncContext = event.getAsyncContext();
            asyncContextAfter(asyncContext,AsyncContextStatus.TIMEOUT);
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            AsyncContext asyncContext = event.getAsyncContext();
            asyncContextAfter(asyncContext,AsyncContextStatus.ERROR);
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
            AsyncContext asyncContext = event.getAsyncContext();
            logger.debug("start Async uri : {}", asyncContext.getRequest().getAttribute(ATTRIBUTE_URI));
        }
    }
    class DefaultAsyncControllerRejectedExecutionHandler implements RejectedExecutionHandler{

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if(r instanceof CanceledCallable) {
                CanceledCallable cc = ((CanceledCallable) r);
                AsyncContext asyncContext = cc.asyncContext;
                asyncContextAfter(asyncContext,AsyncContextStatus.REJECTED);
            }
        }
    }
    enum AsyncContextStatus{
        REJECTED("rejected"),TIMEOUT("timeout"),ERROR("error"),START("start"),COMPLETE("complete");
        private String msg;
        AsyncContextStatus(String msg) {
            this.msg = msg;
        }
        public String value(){
            return msg;
        }

        @Override
        public String toString() {
            return this.msg;
        }
    }
}

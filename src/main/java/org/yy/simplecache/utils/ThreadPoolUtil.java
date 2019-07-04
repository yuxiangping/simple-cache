package org.yy.simplecache.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread pool utils.
 * @author yy
 */
public class ThreadPoolUtil {

  public static ExecutorService createExecutor() {
    return new ThreadPoolExecutor(5, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE >> 15),
        new ThreadFactory() {
          private AtomicInteger cnt = new AtomicInteger(0);

          @Override
          public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Simple Cache Events Thread-" + cnt.incrementAndGet());
            t.setDaemon(true);
            return t;
          }
        });
  }

  public static ScheduledExecutorService createScheduledExecutor() {
    return new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
      private AtomicInteger cnt = new AtomicInteger(0);

      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "Simple Cache Flush Thread-" + cnt.incrementAndGet());
        t.setDaemon(true);
        return t;
      }
    });
  }
}

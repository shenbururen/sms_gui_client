package cn.sanenen.service;

import cn.hutool.log.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 计数静态类 2
 *
 * @author sun
 * 2019年4月23日 下午2:24:42
 */
public class AtomicUtil {
    private static final Log log = Log.get();
    public static AtomicLong sendCount = new AtomicLong();
    public static AtomicLong reponseCount = new AtomicLong();
    public static AtomicLong sucCount = new AtomicLong();
    public static AtomicLong failCount = new AtomicLong();
    public static AtomicLong reportCount = new AtomicLong();

    public static void clear() {
        sendCount.set(0);
        reponseCount.set(0);
        sucCount.set(0);
        failCount.set(0);
        reportCount.set(0);
    }

    static {
        AtomicLong sendCount1 = new AtomicLong();
        AtomicLong reponseCount1 = new AtomicLong();
        AtomicLong sucCount1 = new AtomicLong();
        AtomicLong failCount1 = new AtomicLong();
        AtomicLong reportCount1 = new AtomicLong();
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            if (sendCount.get() == sendCount1.get()
                    && reponseCount.get() == reponseCount1.get()
                    && sucCount.get() == sucCount1.get()
                    && failCount.get() == failCount1.get()
                    && reportCount.get() == reportCount1.get()) {
                return;
            }
            log.info("提交:{}，响应:{}，提交失败:{}，提交成功:{}，状态:{}"
                    , sendCount.get()
                    , reponseCount.get()
                    , failCount.get()
                    , sucCount.get()
                    , reportCount.get()
            );
            sendCount1.set(sendCount.get());
            reponseCount1.set(reponseCount.get());
            sucCount1.set(sucCount.get());
            failCount1.set(failCount.get());
            reportCount1.set(reportCount.get());
        }, 0, 1, TimeUnit.SECONDS);
    }


}

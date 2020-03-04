package cn.sanenen.fx.service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 计数静态类
 */
public class AtomicUtil {

	/**
	 * 发送数量
	 */
	public static AtomicLong sendCount = new AtomicLong();
	/**
	 * 响应数量
	 */
	public static AtomicLong responseCount = new AtomicLong();
	/**
	 * 提交成功
	 */
	public static AtomicLong sucCount = new AtomicLong();
	/**
	 * 提交失败
	 */
	public static AtomicLong failCount = new AtomicLong();
	/**
	 * 状态报告
	 */
	public static AtomicLong reportCount = new AtomicLong();
	/**
	 * 延迟大于一秒
	 */
	public static AtomicLong _1sCount = new AtomicLong();
	/**
	 * 延迟大于三秒
	 */
	public static AtomicLong _3sCount = new AtomicLong();
	/**
	 * 延迟大于10秒
	 */
	public static AtomicLong _10sCount = new AtomicLong();
	/**
	 * 延迟大于30秒
	 */
	public static AtomicLong _30sCount = new AtomicLong();

	
	
	public static void clear() {
		sendCount.set(0);
		responseCount.set(0);
		sucCount.set(0);
		failCount.set(0);
		reportCount.set(0);

		_1sCount.set(0);
		_3sCount.set(0);
		_10sCount.set(0);
		_30sCount.set(0);
	}
}

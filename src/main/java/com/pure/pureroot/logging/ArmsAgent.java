package com.pure.pureroot.logging;

/**
 * 判断 ARMS 探针是否已挂载到当前 JVM。
 * 探针会把 com.apsara.javaagent.* 追加到系统类加载器，未挂载时这些类不存在；
 * 启动参数里带 arms.* 系统属性时也认为探针在场，作为类可见性判断失败时的兜底。
 *
 * <p>为什么需要判断：探针发现日志事件的 MDC 里已经有 trace_id 时会跳过整条注入
 * （trace_id / span_id / trace_flags 都不写），所以有探针时应用不能再往 MDC 写 trace_id。
 */
public final class ArmsAgent {

	private static final String AGENT_CLASS = "com.apsara.javaagent.api.common.AgentInfo";
	private static final String LICENSE_KEY_PROPERTY = "arms.licenseKey";
	private static final String APP_NAME_PROPERTY = "arms.appName";

	private static final boolean PRESENT = detect();

	private ArmsAgent() {
	}

	public static boolean isPresent() {
		return PRESENT;
	}

	private static boolean detect() {
		if (System.getProperty(LICENSE_KEY_PROPERTY) != null || System.getProperty(APP_NAME_PROPERTY) != null) {
			return true;
		}
		try {
			Class.forName(AGENT_CLASS, false, ClassLoader.getSystemClassLoader());
			return true;
		} catch (Throwable ignored) {
			return false;
		}
	}
}

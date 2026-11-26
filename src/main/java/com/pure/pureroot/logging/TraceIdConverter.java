package com.pure.pureroot.logging;

import java.util.Map;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 日志中的 traceId：优先取 ARMS 探针在日志事件 MDC 注入的 trace_id，
 * 没有探针时回退到 TraceIdAspect 写入线程 MDC 的 traceId。
 */
public class TraceIdConverter extends ClassicConverter {

	public static final String ARMS_TRACE_ID = "trace_id";
	public static final String APP_TRACE_ID = "traceId";

	@Override
	public String convert(ILoggingEvent event) {
		Map<String, String> mdc = event.getMDCPropertyMap();
		if (mdc == null) {
			return "";
		}
		String armsTraceId = mdc.get(ARMS_TRACE_ID);
		if (armsTraceId != null && !armsTraceId.isEmpty()) {
			return armsTraceId;
		}
		String appTraceId = mdc.get(APP_TRACE_ID);
		return appTraceId == null ? "" : appTraceId;
	}
}

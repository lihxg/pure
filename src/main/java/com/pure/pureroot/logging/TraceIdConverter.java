package com.pure.pureroot.logging;

import java.util.Map;

import org.slf4j.MDC;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 日志中的 traceId：优先取 ARMS 探针在日志事件 MDC 注入的 trace_id，
 * 其次是 TraceIdAspect 写入线程 MDC 的 traceId，都没有时生成一个并写回线程 MDC。
 */
public class TraceIdConverter extends ClassicConverter {

	public static final String ARMS_TRACE_ID = "trace_id";
	public static final String APP_TRACE_ID = "traceId";

	@Override
	public String convert(ILoggingEvent event) {
		Map<String, String> mdc = event.getMDCPropertyMap();
		if (mdc != null) {
			String armsTraceId = mdc.get(ARMS_TRACE_ID);
			if (armsTraceId != null && !armsTraceId.isEmpty()) {
				return armsTraceId;
			}
			String appTraceId = mdc.get(APP_TRACE_ID);
			if (appTraceId != null && !appTraceId.isEmpty()) {
				return appTraceId;
			}
		}
		return registerGeneratedTraceId();
	}

	private String registerGeneratedTraceId() {
		String existing = MDC.get(APP_TRACE_ID);
		if (existing != null && !existing.isEmpty()) {
			return existing;
		}
		String generated = TraceIds.generate();
		MDC.put(APP_TRACE_ID, generated);
		return generated;
	}
}

package com.pure.pureroot.logging;

import java.util.Map;

import org.slf4j.MDC;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 日志中的 traceId：统一读取 MDC key {@code trace_id}——该值可能来自 ARMS 探针注入，
 * 也可能来自 TraceIdAspect（无探针环境）。都没有时生成一个。
 *
 * <p>探针在场时兜底值写入 {@link #APP_TRACE_ID} 而不是 trace_id：探针发现日志事件已带
 * trace_id 会跳过注入，写 trace_id 会让线上的 span_id / trace_flags 一起丢失。
 */
public class TraceIdConverter extends ClassicConverter {

	/** 与 ARMS 探针一致的 MDC key，日志模板和 Logtail/SLS 都按它取值。 */
	public static final String TRACE_ID = "trace_id";

	/** 探针在场但当前线程没有 span 时的兜底 key，只用于日志输出，不参与探针识别。 */
	public static final String APP_TRACE_ID = "appTraceId";

	private final boolean agentPresent;

	public TraceIdConverter() {
		this(ArmsAgent.isPresent());
	}

	TraceIdConverter(boolean agentPresent) {
		this.agentPresent = agentPresent;
	}

	@Override
	public String convert(ILoggingEvent event) {
		Map<String, String> mdc = event.getMDCPropertyMap();
		if (mdc != null) {
			String traceId = mdc.get(TRACE_ID);
			if (traceId != null && !traceId.isEmpty()) {
				return traceId;
			}
		}
		return registerFallbackTraceId();
	}

	private String registerFallbackTraceId() {
		String key = agentPresent ? APP_TRACE_ID : TRACE_ID;
		String existing = MDC.get(key);
		if (existing != null && !existing.isEmpty()) {
			return existing;
		}
		String generated = TraceIds.generate();
		MDC.put(key, generated);
		return generated;
	}
}

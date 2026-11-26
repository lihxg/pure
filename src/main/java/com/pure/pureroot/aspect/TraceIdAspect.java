package com.pure.pureroot.aspect;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pure.pureroot.logging.TraceIds;

/**
 * 为每个 HTTP 请求生成 traceId 并放入 MDC，由日志模板的 %traceId 输出。
 * 上游已传 X-Trace-Id 时沿用，便于跨服务串联链路。
 */
@Aspect
@Component
public class TraceIdAspect {

	public static final String TRACE_ID = "traceId";
	public static final String TRACE_ID_HEADER = "X-Trace-Id";

	private static final Pattern SAFE_TRACE_ID = Pattern.compile("[A-Za-z0-9_-]{1,64}");

	@Around("@within(org.springframework.web.bind.annotation.RestController)"
			+ " || @within(org.springframework.stereotype.Controller)")
	public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
		String previous = MDC.get(TRACE_ID);
		MDC.put(TRACE_ID, resolveTraceId());
		try {
			return joinPoint.proceed();
		} finally {
			if (previous == null) {
				MDC.remove(TRACE_ID);
			} else {
				MDC.put(TRACE_ID, previous);
			}
		}
	}

	private String resolveTraceId() {
		HttpServletRequest request = currentRequest();
		if (request == null) {
			return newTraceId();
		}
		String fromHeader = request.getHeader(TRACE_ID_HEADER);
		if (fromHeader == null) {
			return newTraceId();
		}
		String trimmed = fromHeader.trim();
		return SAFE_TRACE_ID.matcher(trimmed).matches() ? trimmed : newTraceId();
	}

	private HttpServletRequest currentRequest() {
		if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
			return attributes.getRequest();
		}
		return null;
	}

	private String newTraceId() {
		return TraceIds.generate();
	}
}

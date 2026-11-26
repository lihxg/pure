package com.pure.pureroot.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.pure.pureroot.logging.ArmsAgent;
import com.pure.pureroot.logging.TraceIdConverter;
import com.pure.pureroot.logging.TraceIds;

/**
 * 无 ARMS 探针的环境（如 demo）下，为每个 HTTP 请求生成 traceId 并放入 MDC 的 trace_id，
 * 由日志模板的 %traceId 输出。挂载了探针时不做任何事：探针自己维护 trace_id 与 span_id，
 * 应用再写 trace_id 会让探针跳过日志注入。
 */
@Aspect
@Component
public class TraceIdAspect {

	/** 与 ARMS 探针一致的 MDC key。 */
	public static final String TRACE_ID = TraceIdConverter.TRACE_ID;

	private static final boolean AGENT_PRESENT = ArmsAgent.isPresent();

	@Around("@within(org.springframework.web.bind.annotation.RestController)"
			+ " || @within(org.springframework.stereotype.Controller)")
	public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
		if (AGENT_PRESENT) {
			return joinPoint.proceed();
		}
		String previous = MDC.get(TRACE_ID);
		MDC.put(TRACE_ID, TraceIds.generate());
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
}

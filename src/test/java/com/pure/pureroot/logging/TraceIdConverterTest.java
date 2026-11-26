package com.pure.pureroot.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;

class TraceIdConverterTest {

	private static final String HEX_32 = "[0-9a-f]{32}";

	@BeforeEach
	@AfterEach
	void clearMdc() {
		MDC.clear();
	}

	@Test
	@DisplayName("returns trace_id from the log event MDC, whoever wrote it")
	void convert_traceIdInEventMdc_returnsIt() {
		TraceIdConverter converter = new TraceIdConverter(true);

		assertThat(converter.convert(event(Map.of("trace_id", "4bf92f3577b34da6a3ce929d0e0e4736"))))
				.isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
	}

	@Test
	@DisplayName("ignores the legacy camelCase key")
	void convert_onlyLegacyKey_generatesTraceId() {
		TraceIdConverter converter = new TraceIdConverter(false);

		assertThat(converter.convert(event(Map.of("traceId", "legacy")))).matches(HEX_32);
	}

	@Test
	@DisplayName("agent present: fallback goes to appTraceId so the agent keeps injecting span_id")
	void convert_agentPresentNoTraceId_writesFallbackKeyOnly() {
		TraceIdConverter converter = new TraceIdConverter(true);

		String traceId = converter.convert(event(Map.of()));

		assertThat(traceId).matches(HEX_32);
		assertThat(MDC.get(TraceIdConverter.APP_TRACE_ID)).isEqualTo(traceId);
		assertThat(MDC.get(TraceIdConverter.TRACE_ID)).isNull();
	}

	@Test
	@DisplayName("agent present: reuses the fallback trace id on the same thread")
	void convert_agentPresentTwice_returnsSameTraceId() {
		TraceIdConverter converter = new TraceIdConverter(true);

		assertThat(converter.convert(event(Map.of()))).isEqualTo(converter.convert(event(Map.of())));
	}

	@Test
	@DisplayName("no agent: generates trace_id and registers it in the MDC")
	void convert_noAgent_generatesAndRegistersTraceId() {
		TraceIdConverter converter = new TraceIdConverter(false);

		String traceId = converter.convert(event(Map.of()));

		assertThat(traceId).matches(HEX_32);
		assertThat(MDC.get(TraceIdConverter.TRACE_ID)).isEqualTo(traceId);
	}

	@Test
	@DisplayName("no agent: reuses the generated trace id on the same thread")
	void convert_noAgentTwice_returnsSameTraceId() {
		TraceIdConverter converter = new TraceIdConverter(false);

		assertThat(converter.convert(event(Map.of()))).isEqualTo(converter.convert(event(Map.of())));
	}

	@Test
	@DisplayName("generates a trace id when the event has no MDC map")
	void convert_nullMdc_generatesTraceId() {
		TraceIdConverter converter = new TraceIdConverter(false);
		ILoggingEvent event = mock(ILoggingEvent.class);
		when(event.getMDCPropertyMap()).thenReturn(null);

		assertThat(converter.convert(event)).matches(HEX_32);
	}

	private ILoggingEvent event(Map<String, String> mdc) {
		ILoggingEvent event = mock(ILoggingEvent.class);
		when(event.getMDCPropertyMap()).thenReturn(mdc);
		return event;
	}
}

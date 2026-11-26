package com.pure.pureroot.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;

class TraceIdConverterTest {

	private static final String HEX_32 = "[0-9a-f]{32}";

	private final TraceIdConverter converter = new TraceIdConverter();

	@AfterEach
	void clearMdc() {
		MDC.clear();
	}

	@Test
	@DisplayName("prefers the ARMS trace id injected into the log event MDC")
	void convert_armsTraceIdPresent_returnsArmsTraceId() {
		assertThat(converter.convert(event(Map.of("trace_id", "arms-trace", "traceId", "app-trace"))))
				.isEqualTo("arms-trace");
	}

	@Test
	@DisplayName("falls back to the application trace id when no ARMS agent is attached")
	void convert_onlyAppTraceId_returnsAppTraceId() {
		assertThat(converter.convert(event(Map.of("traceId", "app-trace"))))
				.isEqualTo("app-trace");
	}

	@Test
	@DisplayName("generates a trace id and registers it in the MDC when none is available")
	void convert_noTraceId_generatesAndRegistersInMdc() {
		String traceId = converter.convert(event(Map.of()));

		assertThat(traceId).matches(HEX_32);
		assertThat(MDC.get(TraceIdConverter.APP_TRACE_ID)).isEqualTo(traceId);
	}

	@Test
	@DisplayName("reuses the generated trace id for later events on the same thread")
	void convert_noTraceIdTwice_returnsSameGeneratedTraceId() {
		String first = converter.convert(event(Map.of()));
		String second = converter.convert(event(Map.of()));

		assertThat(second).isEqualTo(first);
	}

	@Test
	@DisplayName("generates a trace id when the event has no MDC map")
	void convert_nullMdc_generatesTraceId() {
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

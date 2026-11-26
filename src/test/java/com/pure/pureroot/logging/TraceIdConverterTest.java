package com.pure.pureroot.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;

class TraceIdConverterTest {

	private final TraceIdConverter converter = new TraceIdConverter();

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
	@DisplayName("returns an empty string when no trace id is available")
	void convert_noTraceId_returnsEmptyString() {
		assertThat(converter.convert(event(Map.of()))).isEmpty();
	}

	@Test
	@DisplayName("returns an empty string when the event has no MDC map")
	void convert_nullMdc_returnsEmptyString() {
		ILoggingEvent event = mock(ILoggingEvent.class);
		when(event.getMDCPropertyMap()).thenReturn(null);

		assertThat(converter.convert(event)).isEmpty();
	}

	private ILoggingEvent event(Map<String, String> mdc) {
		ILoggingEvent event = mock(ILoggingEvent.class);
		when(event.getMDCPropertyMap()).thenReturn(mdc);
		return event;
	}
}

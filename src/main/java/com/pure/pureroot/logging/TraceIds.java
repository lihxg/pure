package com.pure.pureroot.logging;

import java.util.UUID;

/**
 * traceId 生成规则：32 位十六进制，与 ARMS 探针下发的 trace_id 格式保持一致。
 */
public final class TraceIds {

	private TraceIds() {
	}

	public static String generate() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}

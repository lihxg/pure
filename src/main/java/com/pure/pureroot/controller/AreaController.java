package com.pure.pureroot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pure.pureroot.common.ApiResponse;
import com.pure.pureroot.domain.Area;
import com.pure.pureroot.service.AreaService;

import lombok.extern.slf4j.Slf4j;

/**
 * 区域数据查询接口。
 */
@Slf4j
@RestController
@RequestMapping("/area")
public class AreaController {

	private final AreaService areaService;

	public AreaController(AreaService areaService) {
		this.areaService = areaService;
	}

	@GetMapping("/list")
	public ApiResponse<List<Area>> list() {
		List<Area> areas = areaService.list();
		log.info("query area list, size={}", areas.size());
		return ApiResponse.ok(areas);
	}
}

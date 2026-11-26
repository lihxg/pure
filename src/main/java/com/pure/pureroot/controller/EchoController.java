package com.pure.pureroot.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class EchoController {

    @PostMapping(value = "/echo", produces = MediaType.TEXT_PLAIN_VALUE)
    public String echo(@RequestBody EchoRequest request) {
        String value = request.value() == null ? "" : request.value();
        log.info(value);
        return value;
    }

    public record EchoRequest(String key, String value) {
    }
}

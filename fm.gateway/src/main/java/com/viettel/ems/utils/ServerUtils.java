package com.viettel.ems.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Objects;

public class ServerUtils {

    public static String getClientIp(ServerHttpRequest request) {
        return Objects.requireNonNull(request.getRemoteAddress()).getHostString();
    }
}

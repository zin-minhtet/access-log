package com.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClientIpParamsPayloadFilter extends OncePerRequestFilter {
  private static final String MDC_CLIENT_IP = "clientIp";
  private static final String MDC_PARAMS    = "params";
  private static final String MDC_PAYLOAD   = "payload";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

    String xfHeader = wrappedRequest.getHeader("X-Forwarded-For");
    String clientIp = (xfHeader != null && !xfHeader.isBlank())
        ? xfHeader.split(",")[0].trim()
        : wrappedRequest.getRemoteAddr();
    MDC.put(MDC_CLIENT_IP, clientIp);

    Map<String, String[]> paramMap = wrappedRequest.getParameterMap();
    String params = paramMap.entrySet().stream()
        .map(e -> e.getKey() + "=" + String.join(",", e.getValue()))
        .collect(Collectors.joining("&"));
    MDC.put(MDC_PARAMS, params);

    String payload = new String(wrappedRequest.getCachedBody(), StandardCharsets.UTF_8)
        .replaceAll("\\s+", " ");
    MDC.put(MDC_PAYLOAD, payload);

    try {
      filterChain.doFilter(wrappedRequest, response);
    } finally {
      MDC.remove(MDC_CLIENT_IP);
      MDC.remove(MDC_PARAMS);
      MDC.remove(MDC_PAYLOAD);
    }
  }
}

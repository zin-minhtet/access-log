package com.example.aspect;

import com.example.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  private void controller() {
  }

  @Around("controller()")
  public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
    // HTTP method & path
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    String httpInfo = "";
    if (attrs != null) {
      HttpServletRequest req = attrs.getRequest();
      httpInfo = req.getMethod() + " " + req.getRequestURI();
    }

    // MDC: params, payload
    String params = MDC.get("params");
    String payload = MDC.get("payload");
    String sanitizedPayload = payload
        .replaceAll("\"password\": ?\".*?\"", "\"password\": \"****\"")
        .replaceAll("\"token\": ?\".*?\"", "\"token\": \"****\"")
        .replaceAll("\"refreshToken\": ?\".*?\"", "\"refreshToken\": \"****\"");

    // Method signature & json args
    String signature = pjp.getSignature().toShortString();

    // Entry log
    log.info(">>> [{}] → Entering {} params=[{}] payload=[{}]",
        httpInfo, signature, params, sanitizedPayload);

    // Exit log
    try {
      Object result = pjp.proceed();
      String sanitizedResult = JsonUtil.toJson(result)
          .replaceAll("\"access_token\":\".*?\"", "\"access_token\":\"****\"")
          .replaceAll("\"refresh_token\":\".*?\"", "\"refresh_token\":\"****\"")
          .replaceAll("\"list\":\\[.*?]", "\"list\":[ ... ]");

      log.info("<<< [{}] ← Exiting {} with result=[{}]",
          httpInfo, signature, sanitizedResult);

      return result;
    } catch (Throwable ex) {
      log.error("!!! [{}] ← Exception in {}: {}",
          httpInfo, signature, ex.getMessage(), ex);
      throw ex;
    }
  }
}

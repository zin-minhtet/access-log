package com.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      return "Error serializing object: " + e.getMessage();
    }
  }
}

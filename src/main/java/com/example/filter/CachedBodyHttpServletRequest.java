package com.example.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
  private final byte[] cachedBody;

  public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
  }

  @Override
  public ServletInputStream getInputStream() {
    ByteArrayInputStream byteStream = new ByteArrayInputStream(cachedBody);
    return new ServletInputStream() {
      @Override public boolean isFinished() { return byteStream.available() == 0; }
      @Override public boolean isReady() { return true; }
      @Override public void setReadListener(ReadListener rl) { /* no-op */ }
      @Override public int read() throws IOException { return byteStream.read(); }
    };
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
  }

  public byte[] getCachedBody() {
    return cachedBody;
  }
}

package com.example.controller;

import com.example.model.dtos.TestRequest;
import jakarta.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
  @GetMapping
  public ResponseEntity<String> test(@RequestParam("name") @Nullable String name) {
    boolean isNameEmpty = name == null || name.isEmpty();
    if (isNameEmpty) {
      return ResponseEntity.ok("Hello");
    } else {
      return ResponseEntity.ok("Hello, " + name);
    }
  }

  @PostMapping
  public ResponseEntity<String> test(@RequestBody TestRequest req) {
    boolean isNameEmpty = req.getName() == null || req.getName().isEmpty();
    if (isNameEmpty) {
      return ResponseEntity.badRequest().body("Hello");
    } else {
      return ResponseEntity.ok("Hello, " + req.getName());
    }
  }
}

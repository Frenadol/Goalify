package com.frenadol.goalify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class testController {

    @PostMapping("/test-json")
    public ResponseEntity<Map<String, String>> receiveJson(@RequestBody Map<String, String> payload) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "JSON recibido correctamente");
        response.put("data", payload.toString());
        return ResponseEntity.ok(response);
    }
}
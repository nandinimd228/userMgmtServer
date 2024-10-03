package com.example.server.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.entity.UserAccount;
import com.example.server.service.UserAccountService;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Autowired
    private UserAccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserAccount loginRequest) {
        try {
            UserAccount authenticatedAccount = accountService.authenticate(loginRequest.getEmail(),
                    loginRequest.getPassword());

            Map<String, String> response = new HashMap<>();
            response.put("accountId", authenticatedAccount.getId().toString());
            response.put("email", authenticatedAccount.getEmail());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

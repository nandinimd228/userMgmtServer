package com.example.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.entity.UserAccount;
import com.example.server.exception.EmailAlreadyExistsException;
import com.example.server.exception.UserAccountNotFoundException;
import com.example.server.service.UserAccountService;
import java.util.Collections;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/userAccount")
public class UserAccountController {

    @Autowired
    private UserAccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody UserAccount account) {
        try {
            UserAccount createdAccount = accountService.createAccount(account);
            return ResponseEntity.ok(createdAccount);
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserAccount>> getAllAccounts() {
        List<UserAccount> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        try {
            UserAccount account = accountService.getAccountById(id);
            return ResponseEntity.ok(account);
        } catch (UserAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id,
            @Valid @RequestBody UserAccount accountDetails) {
        try {
            UserAccount updatedAccount = accountService.updateAccount(id, accountDetails);
            return ResponseEntity.ok(updatedAccount);
        } catch (UserAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (UserAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable Long id,
            @Valid @RequestBody String password) {
        try {
            UserAccount updatedAccount = accountService.updatePassword(id, password);
            return ResponseEntity.ok(updatedAccount);
        } catch (UserAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
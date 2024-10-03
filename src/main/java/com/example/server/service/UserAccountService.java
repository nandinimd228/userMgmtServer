package com.example.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.server.entity.UserAccount;
import com.example.server.exception.EmailAlreadyExistsException;
import com.example.server.exception.InvalidCredentialsException;
import com.example.server.exception.UserAccountNotFoundException;
import com.example.server.repository.UserAccountRepository;

import jakarta.validation.Valid;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository accountRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserAccount createAccount(UserAccount account) {
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new EmailAlreadyExistsException("Email id exists");
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public List<UserAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    public UserAccount getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new UserAccountNotFoundException());
    }

    public UserAccount getAccountByName(String name) {
        return accountRepository.findByEmail(name).orElseThrow(() -> new UserAccountNotFoundException());
    }

    public UserAccount updateAccount(Long id, UserAccount accountDetails) {
        UserAccount account = getAccountById(id);
        account.setName(accountDetails.getName());
        account.setEmail(accountDetails.getEmail());
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
        } else {
            throw new UserAccountNotFoundException();
        }
    }

    public UserAccount authenticate(String email, String password) {
        Optional<UserAccount> accountOptional = accountRepository.findByEmail(email);
        if (accountOptional.isPresent()) {
            UserAccount account = accountOptional.get();
            if (passwordEncoder.matches(password, account.getPassword())) {
                return account;
            } else {
                throw new InvalidCredentialsException();
            }
        }
        throw new UserAccountNotFoundException();
    }

    public UserAccount updatePassword(Long id, @Valid String password) {
        UserAccount account = getAccountById(id);
        if (password != null && !password.isEmpty()) {
            account.setPassword(passwordEncoder.encode(password));
        }

        return accountRepository.save(account);
    }
}
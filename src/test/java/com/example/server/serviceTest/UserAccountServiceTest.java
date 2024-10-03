package com.example.server.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.example.server.entity.UserAccount;
import com.example.server.exception.EmailAlreadyExistsException;
import com.example.server.exception.InvalidCredentialsException;
import com.example.server.exception.UserAccountNotFoundException;
import com.example.server.repository.UserAccountRepository;
import com.example.server.service.UserAccountService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTest {

    @InjectMocks
    private UserAccountService userAccountService;

    @Mock
    private UserAccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testCreateAccount_Success() {
        UserAccount account = new UserAccount();
        account.setEmail("test@example.com");
        account.setPassword("password123");

        when(accountRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(accountRepository.save(any(UserAccount.class))).thenReturn(account);

        UserAccount createdAccount = userAccountService.createAccount(account);

        verify(accountRepository, times(1)).save(any(UserAccount.class));
        assertEquals("test@example.com", createdAccount.getEmail());
    }

    @Test
    public void testCreateAccount_EmailAlreadyExists() {
        UserAccount account = new UserAccount();
        account.setEmail("existing@example.com");

        when(accountRepository.existsByEmail("existing@example.com")).thenReturn(true);

        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userAccountService.createAccount(account);
        });

        assertEquals("Email id exists", exception.getMessage());
        verify(accountRepository, never()).save(any(UserAccount.class));
    }

    @Test
    public void testGetAllAccounts() {
        UserAccount account1 = new UserAccount();
        account1.setEmail("test1@example.com");

        UserAccount account2 = new UserAccount();
        account2.setEmail("test2@example.com");

        when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));

        List<UserAccount> accounts = userAccountService.getAllAccounts();

        assertEquals(2, accounts.size());
        assertEquals("test1@example.com", accounts.get(0).getEmail());
    }

    @Test
    public void testGetAccountById_Success() {
        UserAccount account = new UserAccount();
        account.setEmail("test@example.com");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        UserAccount foundAccount = userAccountService.getAccountById(1L);

        assertEquals("test@example.com", foundAccount.getEmail());
    }

    @Test
    public void testGetAccountById_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserAccountNotFoundException.class, () -> {
            userAccountService.getAccountById(1L);
        });

        assertEquals("User account not found", exception.getMessage());
    }

    @Test
    public void testGetAccountByName_Success() {
        UserAccount account = new UserAccount();
        account.setEmail("test@example.com");

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));

        UserAccount foundAccount = userAccountService.getAccountByName("test@example.com");

        assertEquals("test@example.com", foundAccount.getEmail());
    }

    @Test
    public void testGetAccountByName_NotFound() {
        when(accountRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserAccountNotFoundException.class, () -> {
            userAccountService.getAccountByName("nonexistent@example.com");
        });

        assertEquals("User account not found", exception.getMessage());
    }

    @Test
    public void testUpdateAccount_Success() {
        UserAccount existingAccount = new UserAccount();
        existingAccount.setId(1L);
        existingAccount.setEmail("old@example.com");

        UserAccount updatedDetails = new UserAccount();
        updatedDetails.setEmail("new@example.com");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(UserAccount.class))).thenReturn(updatedDetails);

        UserAccount updatedAccount = userAccountService.updateAccount(1L, updatedDetails);

        assertEquals("new@example.com", updatedAccount.getEmail());
    }

    @Test
    public void testUpdateAccount_UpdatePassword() {
        UserAccount existingAccount = new UserAccount();
        existingAccount.setId(1L);
        existingAccount.setEmail("old@example.com");
        existingAccount.setPassword("oldPassword");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(accountRepository.save(any(UserAccount.class))).thenReturn(existingAccount);

        UserAccount updatedAccount = userAccountService.updatePassword(1L, "newPassword");

        assertEquals("encodedNewPassword", updatedAccount.getPassword());
        verify(accountRepository, times(1)).save(updatedAccount);
    }

    @Test
    public void testUpdateAccount_UpdatePasswordEmpty() {
        UserAccount existingAccount = new UserAccount();
        existingAccount.setId(1L);
        existingAccount.setEmail("old@example.com");
        existingAccount.setPassword("oldPassword");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(UserAccount.class))).thenReturn(existingAccount);

        UserAccount updatedAccount = userAccountService.updatePassword(1L, "");

        assertEquals("oldPassword", updatedAccount.getPassword());
        verify(accountRepository, times(1)).save(updatedAccount);
    }

    @Test
    public void testUpdateAccount_UpdatePasswordNull() {
        UserAccount existingAccount = new UserAccount();
        existingAccount.setId(1L);
        existingAccount.setEmail("old@example.com");
        existingAccount.setPassword("oldPassword");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(UserAccount.class))).thenReturn(existingAccount);

        UserAccount updatedAccount = userAccountService.updatePassword(1L, null);

        assertEquals("oldPassword", updatedAccount.getPassword());
        verify(accountRepository, times(1)).save(updatedAccount);
    }

    @Test
    public void testUpdateAccount_NotFound() {
        UserAccount updatedDetails = new UserAccount();

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserAccountNotFoundException.class, () -> {
            userAccountService.updateAccount(1L, updatedDetails);
        });

        assertEquals("User account not found", exception.getMessage());
    }

    @Test
    public void testDeleteAccount_Success() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        userAccountService.deleteAccount(1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteAccount_NotFound() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(UserAccountNotFoundException.class, () -> {
            userAccountService.deleteAccount(1L);
        });

        assertEquals("User account not found", exception.getMessage());
        verify(accountRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testAuthenticate_Success() {
        UserAccount account = new UserAccount();
        account.setEmail("test@example.com");
        account.setPassword("encodedPassword");

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password123", account.getPassword())).thenReturn(true);

        UserAccount authenticatedAccount = userAccountService.authenticate("test@example.com", "password123");

        assertEquals("test@example.com", authenticatedAccount.getEmail());
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        UserAccount account = new UserAccount();
        account.setEmail("test@example.com");
        account.setPassword("encodedPassword");

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrongPassword", account.getPassword())).thenReturn(false);

        Exception exception = assertThrows(InvalidCredentialsException.class, () -> {
            userAccountService.authenticate("test@example.com", "wrongPassword");
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    public void testAuthenticate_UserNotFound() {
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserAccountNotFoundException.class, () -> {
            userAccountService.authenticate("test@example.com", "password123");
        });

        assertEquals("User account not found", exception.getMessage());
    }
}

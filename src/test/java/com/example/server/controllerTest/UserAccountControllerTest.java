package com.example.server.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.server.controller.UserAccountController;
import com.example.server.entity.UserAccount;
import com.example.server.exception.EmailAlreadyExistsException;
import com.example.server.exception.UserAccountNotFoundException;
import com.example.server.service.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserAccountController.class)
@ExtendWith(MockitoExtension.class)
public class UserAccountControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @InjectMocks
        private UserAccountController userAccountController;

        @MockBean
        private UserAccountService accountService;

        @BeforeEach
        public void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(userAccountController).build();
        }

        @Test
        public void testCreateAccount() throws Exception {
                UserAccount userAccount = new UserAccount();
                userAccount.setName("Test User");
                userAccount.setEmail("test2@example.com");
                userAccount.setPassword("password123");

                when(accountService.createAccount(any(UserAccount.class))).thenReturn(userAccount);

                mockMvc.perform(post("/api/v1/userAccount")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userAccount)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("test2@example.com"));
        }

        @Test
        public void testCreateAccount_EmailAlreadyExists() throws Exception {
                UserAccount userAccount = new UserAccount();
                userAccount.setName("Test User");
                userAccount.setEmail("test2@example.com");
                userAccount.setPassword("password123");

                when(accountService.createAccount(any(UserAccount.class)))
                                .thenThrow(new EmailAlreadyExistsException("Email id exists"));

                mockMvc.perform(post("/api/v1/userAccount")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userAccount)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("Email id exists"));
        }

        @Test
        public void testGetAllAccounts() throws Exception {
                UserAccount account1 = new UserAccount();
                account1.setId(1L);
                account1.setEmail("test1@example.com");

                UserAccount account2 = new UserAccount();
                account2.setId(2L);
                account2.setEmail("test2@example.com");

                List<UserAccount> accounts = Arrays.asList(account1, account2);

                when(accountService.getAllAccounts()).thenReturn(accounts);

                mockMvc.perform(get("/api/v1/userAccount"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1L))
                                .andExpect(jsonPath("$[0].email").value("test1@example.com"))
                                .andExpect(jsonPath("$[1].id").value(2L))
                                .andExpect(jsonPath("$[1].email").value("test2@example.com"));
        }

        @Test
        public void testGetAccountById() throws Exception {
                UserAccount account = new UserAccount();
                account.setId(1L);
                account.setEmail("test@example.com");

                when(accountService.getAccountById(1L)).thenReturn(account);

                mockMvc.perform(get("/api/v1/userAccount/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        public void testGetAccountById_NotFound() throws Exception {
                when(accountService.getAccountById(1L)).thenThrow(new UserAccountNotFoundException());

                mockMvc.perform(get("/api/v1/userAccount/1"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("User account not found"));
        }

        @Test
        public void testUpdateAccount() throws Exception {
                UserAccount account = new UserAccount();
                account.setId(1L);
                account.setEmail("updated@example.com");

                when(accountService.updateAccount(any(Long.class), any(UserAccount.class))).thenReturn(account);

                String updatedAccountJson = "{\"name\":\"John Doe\",\"email\":\"updated@example.com\",\"password\":\"newPassword\"}";

                mockMvc.perform(put("/api/v1/userAccount/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedAccountJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.email").value("updated@example.com"));
        }

        @Test
        public void testUpdateAccount_NotFound() throws Exception {
                String updatedAccountJson = "{\"name\":\"John Doe\",\"email\":\"updated@example.com\",\"password\":\"newPassword\"}";

                when(accountService.updateAccount(any(Long.class), any(UserAccount.class)))
                                .thenThrow(new UserAccountNotFoundException());

                mockMvc.perform(put("/api/v1/userAccount/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedAccountJson))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("User account not found"));
        }

        @Test
        public void testDeleteAccount_Success() throws Exception {
                mockMvc.perform(delete("/api/v1/userAccount/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        public void testDeleteAccount_NotFound() throws Exception {
                doThrow(new UserAccountNotFoundException()).when(accountService).deleteAccount(1L);

                mockMvc.perform(delete("/api/v1/userAccount/1"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("User account not found"));
        }

        @Test
        public void testUpdatePassword() throws Exception {
                UserAccount account = new UserAccount();
                account.setId(1L);
                account.setEmail("updated@example.com");

                when(accountService.updatePassword(any(Long.class), any(String.class))).thenReturn(account);

                String newPassword = "newPassword";

                mockMvc.perform(patch("/api/v1/userAccount/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newPassword))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.email").value("updated@example.com"));
        }

        @Test
        public void testUpdatePassword_NotFound() throws Exception {
                String newPassword = "newPassword";

                when(accountService.updatePassword(any(Long.class), any(String.class)))
                                .thenThrow(new UserAccountNotFoundException());

                mockMvc.perform(patch("/api/v1/userAccount/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newPassword))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("User account not found"));
        }
}
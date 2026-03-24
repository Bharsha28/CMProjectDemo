package com.CardMaster.controller.iam;

import com.CardMaster.Enum.iam.UserEnum;
import com.CardMaster.dto.iam.LoginRequestDto;
import com.CardMaster.dto.iam.ResponseStructure;
import com.CardMaster.dto.iam.UserDto;
import com.CardMaster.model.iam.User;
import com.CardMaster.security.iam.JwtUtil;
import com.CardMaster.service.iam.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setName("Priya");
        user.setRole(UserEnum.CUSTOMER);
        user.setEmail("priya@example.com");
        user.setPhone("9876543210");
        user.setPassword("secret123");

        userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setName("Priya");
        userDto.setEmail("priya@example.com");
        userDto.setPhone("9876543210");
        userDto.setRole(UserEnum.CUSTOMER);

    }

    @Test
    void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        ResponseEntity<ResponseStructure<List<UserDto>>> result = userController.getAllUsers();

        assertEquals("Users Retrieved Successfully", result.getBody().getMsg());
        assertEquals(1, result.getBody().getData().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<ResponseStructure<UserDto>> result = userController.getUserById(1L);

        assertEquals("User Retrieved Successfully", result.getBody().getMsg());
        assertEquals("Priya", result.getBody().getData().getName());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testRegisterUser() {
        when(userService.registerUser(user)).thenReturn(user);

        ResponseEntity<ResponseStructure<UserDto>> result = userController.register(user);

        assertEquals("User created successfully", result.getBody().getMsg());
        assertEquals("Priya", result.getBody().getData().getName());
        verify(userService, times(1)).registerUser(user);
    }

    @Test
    void testLoginUser() {
        LoginRequestDto loginRequest = new LoginRequestDto("priya@example.com", "secret123");
        when(userService.loginUser("priya@example.com", "secret123")).thenReturn("mockToken");

        ResponseEntity<ResponseStructure<String>> result = userController.login(loginRequest);

        assertEquals("Login Successful", result.getBody().getMsg());
        assertEquals("Bearer mockToken", result.getBody().getData());
        verify(userService, times(1)).loginUser("priya@example.com", "secret123");
    }

    @Test
    void testLogoutUser() {
        when(jwtUtil.extractUserId("mockToken")).thenReturn(1L);

        ResponseEntity<ResponseStructure<String>> result =
                userController.logout("Bearer mockToken");

        assertEquals("Logout Successful", result.getBody().getMsg());
        assertEquals("Goodbye 1", result.getBody().getData());
        verify(jwtUtil, times(1)).extractUserId("mockToken");
        verify(userService, times(1)).logoutUser(1L);
    }
}

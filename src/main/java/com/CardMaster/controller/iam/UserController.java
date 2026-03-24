package com.CardMaster.controller.iam;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CardMaster.dto.iam.ResponseStructure;
import com.CardMaster.dto.iam.UserDto;
import com.CardMaster.dto.iam.LoginRequestDto;
import com.CardMaster.mapper.iam.UserMapper;
import com.CardMaster.model.iam.User;
import com.CardMaster.security.iam.JwtUtil;
import com.CardMaster.service.iam.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LogManager.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // GET all users
    @GetMapping
    public ResponseEntity<ResponseStructure<List<UserDto>>> getAllUsers() {
        log.info("Inside getAllUsers Controller");
        List<UserDto> users = userService.getAllUsers();

        ResponseStructure<List<UserDto>> res = new ResponseStructure<>();
        res.setMsg("Users Retrieved Successfully");
        res.setData(users);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // GET user by userId
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseStructure<UserDto>> getUserById(@PathVariable Long userId) {
        log.info("Inside getUserById Controller");
        User user = userService.getUserById(userId);

        ResponseStructure<UserDto> res = new ResponseStructure<>();
        res.setMsg("User Retrieved Successfully");
        res.setData(UserMapper.toDto(user));
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // POST register new user
    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<UserDto>> register(@Valid @RequestBody User user) {
        log.info("Inside register user Controller");
        User saved = userService.registerUser(user);

        ResponseStructure<UserDto> resp = new ResponseStructure<>();
        resp.setMsg("User created successfully");
        resp.setData(UserMapper.toDto(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // POST login (using email + password)
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<String>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Inside login Controller");

        String token = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());

        ResponseStructure<String> r = new ResponseStructure<>();
        r.setMsg("Login Successful");
        r.setData("Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).body(r);
    }

    // POST logout
    @PostMapping("/logout")
    public ResponseEntity<ResponseStructure<String>> logout(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.substring(7)); //  returns Long
        userService.logoutUser(userId); //  pass directly

        ResponseStructure<String> r = new ResponseStructure<>();
        r.setMsg("Logout Successful");
        r.setData("Goodbye " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(r);
    }

}

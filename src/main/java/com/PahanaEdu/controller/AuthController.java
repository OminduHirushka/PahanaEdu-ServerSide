package com.PahanaEdu.controller;

import com.PahanaEdu.config.JwtProvider;
import com.PahanaEdu.dto.UserDTO;
import com.PahanaEdu.dto.auth.AuthResponse;
import com.PahanaEdu.dto.auth.LoginRequest;
import com.PahanaEdu.exception.DuplicateResourceException;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.User;
import com.PahanaEdu.repository.UserRepository;
import com.PahanaEdu.service.impl.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/auth")
public class AuthController {
    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByAccountNumber(userDTO.getAccountNumber()).isPresent()) {
            throw new DuplicateResourceException("User already exists");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByContact(userDTO.getContact())) {
            throw new DuplicateResourceException("Contact already exists");
        }
        if (userRepository.existsByNic(userDTO.getNic())) {
            throw new DuplicateResourceException("Nic already exists");
        }

        User user = new User();
        user.setAccountNumber(userDTO.getAccountNumber());
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setContact(userDTO.getContact());
        user.setNic(userDTO.getNic());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole());

        User savedUser = userRepository.save(user);


        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(savedUser.getRole().name()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setRole(savedUser.getRole());
        authResponse.setMessage("Registered Successfully");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        try {
            Authentication authentication = authenticate(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtProvider.generateToken(authentication);

            User user = userRepository.findByEmail(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            authResponse.setUsername(username);
            authResponse.setRole(user.getRole());
            authResponse.setMessage("Login successful");

            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails;

        try {
            userDetails = userDetailService.loadUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw new BadCredentialsException("Invalid Username");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

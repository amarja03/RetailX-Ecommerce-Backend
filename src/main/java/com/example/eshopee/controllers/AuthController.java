package com.example.eshopee.controllers;

import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eshopee.exceptions.UserNotFoundException;
import com.example.eshopee.payloads.LoginCredentials;
import com.example.eshopee.payloads.UserDTO;
import com.example.eshopee.filter.JWTUtil;
import com.example.eshopee.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private final JWTUtil jwtUtil;

	private final AuthenticationManager authenticationManager;

	public AuthController(UserService userService, PasswordEncoder passwordEncoder, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerHandler(@Valid @RequestBody UserDTO user) throws UserNotFoundException {
		String encodedPass = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPass);

		userService.registerUser(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtil.generateToken();
		return ResponseEntity.status(HttpStatus.CREATED).body("Token : " + token);
	}

	@PostMapping("/login")
	public Map<String, Object> loginHandler(@Valid @RequestBody LoginCredentials credentials) {

		UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
				credentials.getEmail(), credentials.getPassword());

		Authentication authentication = authenticationManager.authenticate(authCredentials);


		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtil.generateToken();

		return Collections.singletonMap("jwt-token", token);
	}
}
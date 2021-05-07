package ie.tcd.cs7cs3.wayfinding.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import ie.tcd.cs7cs3.wayfinding.server.security.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import ie.tcd.cs7cs3.wayfinding.server.model.Role;
import ie.tcd.cs7cs3.wayfinding.server.model.RolesEnum;
import ie.tcd.cs7cs3.wayfinding.server.model.User;
import ie.tcd.cs7cs3.wayfinding.server.repository.RoleRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.UserRepository;
import ie.tcd.cs7cs3.wayfinding.server.requests.LoginRequest;
import ie.tcd.cs7cs3.wayfinding.server.requests.RegisterRequest;
import ie.tcd.cs7cs3.wayfinding.server.response.JwtResponse;
import ie.tcd.cs7cs3.wayfinding.server.response.MessageResponse;
import ie.tcd.cs7cs3.wayfinding.server.security.jwt.AuthTokenFilter;
import ie.tcd.cs7cs3.wayfinding.server.security.jwt.JwtUtils;
import ie.tcd.cs7cs3.wayfinding.server.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.getEmailId())) {
			logger.debug("Email is already in use, please use another email id");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(-1, "Error: Email is already in use!"));
		}

		User user = new User(signUpRequest.getEmailId(),
							 encoder.encode(signUpRequest.getPassword()));
		Set<Role> roles = new HashSet<>();
		Role userRole = roleRepository.findByName(RolesEnum.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse(0, "User registered successfully!"));
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(),
												 userDetails.getEmail(), 
												 roles));
	}
}

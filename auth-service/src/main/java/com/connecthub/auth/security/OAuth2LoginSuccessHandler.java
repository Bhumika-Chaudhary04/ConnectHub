package com.connecthub.auth.security;

import com.connecthub.auth.entity.AuthProvider;
import com.connecthub.auth.entity.User;
import com.connecthub.auth.entity.UserStatus;
import com.connecthub.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

		String email = oauthUser.getAttribute("email");
		String fullName = oauthUser.getAttribute("name");
		String avatarUrl = oauthUser.getAttribute("picture");

		User user = userRepository.findByEmail(email).orElseGet(() -> {
			String username = email.split("@")[0];

			User newUser = User.builder().email(email).username(generateUniqueUsername(username)).fullName(fullName)
					.avatarUrl(avatarUrl).passwordHash(null).provider(AuthProvider.GOOGLE).status(UserStatus.ONLINE)
					.isActive(true).build();

			return userRepository.save(newUser);
		});

		String token = jwtService.generateToken(user.getUserId(), user.getEmail());
		response.getWriter().write("Google login successful. Token: " + token);
	}

	private String generateUniqueUsername(String baseUsername) {
		String username = baseUsername;
		int count = 1;

		while (userRepository.existsByUsername(username)) {
			username = baseUsername + count;
			count++;
		}

		return username;
	}
}
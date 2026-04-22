package com.connecthub.auth.security;

import com.connecthub.auth.entity.AuthProvider;
import com.connecthub.auth.entity.User;
import com.connecthub.auth.entity.UserStatus;
import com.connecthub.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JwtService jwtService;

	@Value("${app.frontend.oauth-success-url:http://localhost:3000/oauth-success?token=}")
	private String frontendRedirect;

	public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtService jwtService) {
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

		String email = oauthUser.getAttribute("email");
		String name = oauthUser.getAttribute("name");
		String avatar = oauthUser.getAttribute("picture");

		if (email == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not available from Google");
			return;
		}

		AuthProvider provider = AuthProvider.GOOGLE;
		final String finalEmail = email;
		final String finalAvatar = avatar;

		User user = userRepository.findByEmail(finalEmail)
				.orElseGet(() -> createNewUser(finalEmail, name, finalAvatar, provider));

		if (avatar != null && !avatar.equals(user.getAvatarUrl())) {
			user.setAvatarUrl(avatar);
			userRepository.save(user);
		}

		String token = jwtService.generateToken(user.getUserId(), user.getEmail());
		String redirectUrl = frontendRedirect + URLEncoder.encode(token, StandardCharsets.UTF_8);

		response.sendRedirect(redirectUrl);
	}

	private User createNewUser(String email, String name, String avatar, AuthProvider provider) {
		String username = generateUsername(email);

		User user = User.builder().email(email).username(username).fullName(name != null ? name : username)
				.avatarUrl(avatar).passwordHash(null).provider(provider).status(UserStatus.ONLINE).isActive(true)
				.build();

		return userRepository.save(user);
	}

	private String generateUsername(String email) {
		String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_");
		String username = base;
		int count = 1;

		while (userRepository.existsByUsername(username)) {
			username = base + count;
			count++;
		}

		return username;
	}
}

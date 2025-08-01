package entus.authServer.service.authorization;

import entus.authServer.domain.user.User;
import entus.authServer.domain.user.local.CustomUserDetails;
import entus.authServer.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 로그인 시 JWT 발급 로직
 * 프레임워크 내부적 실행이라 명시적으로 json 작성
 */
@Service
@RequiredArgsConstructor
public class JwtHandler implements AuthenticationSuccessHandler {
    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String name = authentication.getName();
        User user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        String accessToken = jwtGenerator.generateAccessToken(user);
        String refreshToken = jwtGenerator.generateRefreshToken(user);

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        String redirectUrl = request.getParameter("redirectUrl");
        if(redirectUrl != null)
            response.sendRedirect(URLDecoder.decode(redirectUrl));
        else
            response.sendRedirect("http://localhost:8080/user");
    }
}

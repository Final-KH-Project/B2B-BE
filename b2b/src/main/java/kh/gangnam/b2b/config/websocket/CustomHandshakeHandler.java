package kh.gangnam.b2b.config.websocket;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String accessToken = (String) attributes.get("access");
        if (accessToken != null) {
            CustomEmployeeDetails userDetails = jwtTokenProvider.getUserDetailsFromToken(accessToken);
            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
        }
        return null;
    }
}

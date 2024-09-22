package com.ku.covigator.security.jwt;

import com.ku.covigator.exception.jwt.JwtExpiredException;
import com.ku.covigator.exception.jwt.JwtInvalidException;
import com.ku.covigator.exception.jwt.JwtNotFoundException;
import com.ku.covigator.exception.jwt.JwtUnsupportedTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompJwtInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            String token = jwtProvider.getTokenFromRequestHeader(authorizationHeader);

            if(jwtProvider.validateToken(token)) {
                String memberId = jwtProvider.getPrincipal(token);
                accessor.getSessionAttributes().put("memberId", memberId);
            }
        }

        return message;
    }

}
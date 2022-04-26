package me.study.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.study.gateway.exception.ExceptionResponse;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment env;
    private final ObjectMapper objectMapper;

    public AuthorizationHeaderFilter(Environment env, ObjectMapper objectMapper) {
        super(Config.class);
        this.env = env;
        this.objectMapper= objectMapper;
    }

    @Data
    public static class Config{}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String requestId = request.getId();

            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "Unauthorized","No Authorization header"
                                , requestId, HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            String userId = isJwtValid(jwt);

            if(userId == null){
                return onError(exchange, "Unauthorized","JWT Token is not valid"
                                , requestId, HttpStatus.UNAUTHORIZED);
            }

            addAuthorizationHeaders(request, userId);

            return chain.filter(exchange);
        };
    }

    private String isJwtValid(String jwt) {
        String subject = null;

        try{
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        }catch (Exception e){}

        if(subject == null || subject.isEmpty()){
            subject = null;
        }

        return subject;
    }

    private void addAuthorizationHeaders(ServerHttpRequest request, String userId) {
        request.mutate()
                .header("X-Authorization-Id", userId)
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, String message, String requestId, HttpStatus httpStatus) {
        log.error(String.format("[%s] %s", requestId, message));

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                                                                .timestamp(new Date())
                                                                .status(httpStatus.value())
                                                                .error(error)
                                                                .message(message)
                                                                .path(exchange.getRequest().getURI().toString())
                                                                .build();

        byte[] errorByte = null;
        try{
            errorByte = objectMapper.writeValueAsBytes(exceptionResponse);
        }catch (JsonProcessingException e){
            errorByte = e.getMessage().getBytes();
        }

        ServerHttpResponse response = exchange.getResponse();
        DataBuffer buffer = response.bufferFactory().wrap(errorByte);
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(buffer));
    }
}

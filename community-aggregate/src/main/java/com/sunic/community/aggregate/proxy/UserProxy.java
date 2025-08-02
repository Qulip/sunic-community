package com.sunic.community.aggregate.proxy;

import com.sunic.community.spec.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProxy {

    private final WebClient webClient;

    @Value("${user.client.base-url:http://localhost:8080}")
    private String userServiceBaseUrl;

    /**
     * Check if a user exists and is valid
     *
     * @param userId the user ID to check
     * @return true if user is valid, false otherwise
     */
    public boolean checkUser(Integer userId) {
        try {
            ApiResponse<Boolean> response = webClient
                .get()
                .uri(userServiceBaseUrl + "/user/client/check/{userId}", userId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .cast(ApiResponse.class)
                .block();

            return response != null && response.isSuccess() && Boolean.TRUE.equals(response.getData());
        } catch (Exception e) {
            log.error("Failed to check user validity for userId: {}", userId, e);
            return false;
        }
    }

    /**
     * Check if a user is an administrator
     *
     * @param userId the user ID to check
     * @return true if user is admin, false otherwise
     */
    public boolean checkUserIsAdmin(Integer userId) {
        try {
            ApiResponse<Boolean> response = webClient
                .get()
                .uri(userServiceBaseUrl + "/user/client/checkAdmin/{userId}", userId)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .cast(ApiResponse.class)
                .block();

            return response != null && response.isSuccess() && Boolean.TRUE.equals(response.getData());
        } catch (Exception e) {
            log.error("Failed to check admin status for userId: {}", userId, e);
            return false;
        }
    }
}
package com.sunic.community.aggregate.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.sunic.community.spec.common.ApiResponse;
import com.sunic.community.spec.common.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProxy {

    private final WebClient webClient;

    @Value("${user.client.base-url:http://localhost:8080}")
    private String userServiceBaseUrl;

    public void validateUser(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("User ID is required");
        }
        if (!checkUser(userId)) {
            throw new UnauthorizedException("Valid user required for this operation");
        }
    }

    public void validateAdminUser(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("User ID is required");
        }
        if (!checkUserIsAdmin(userId)) {
            throw new UnauthorizedException("Admin privileges required for this operation");
        }
    }

    /**
     * Check if a user exists and is valid
     *
     * @param userId the user ID to check
     * @return true if user is valid, false otherwise
     */
    private boolean checkUser(Integer userId) {
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
    private boolean checkUserIsAdmin(Integer userId) {
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
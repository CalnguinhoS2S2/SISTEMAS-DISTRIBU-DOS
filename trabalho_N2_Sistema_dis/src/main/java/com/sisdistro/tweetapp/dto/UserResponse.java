package com.sisdistro.tweetapp.dto;

/**
 * Representa a resposta enviada pela API para um usuário.
 */
public record UserResponse(
        Long id,
        String handle,
        String displayName,
        String profileImageUrl,
        String bio,
        long tweetCount) {
}

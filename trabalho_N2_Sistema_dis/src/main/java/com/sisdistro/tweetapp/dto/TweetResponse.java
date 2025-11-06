package com.sisdistro.tweetapp.dto;

import java.time.LocalDateTime;

/**
 * Representa a resposta da API para um tweet.
 */
public record TweetResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long authorId,
        String authorHandle,
        String authorDisplayName) {
}

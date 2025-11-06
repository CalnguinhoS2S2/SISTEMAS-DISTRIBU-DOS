package com.sisdistro.tweetapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados necessários para criar ou atualizar um tweet.
 */
public record TweetRequest(
        @NotBlank(message = "O @handle é obrigatório.")
        @Size(max = 40, message = "O @handle deve ter no máximo 40 caracteres.")
        String authorHandle,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, max = 120, message = "A senha deve ter entre 6 e 120 caracteres.")
        String password,

        @NotBlank(message = "O conteúdo do tweet é obrigatório.")
        @Size(max = 280, message = "O tweet deve ter no máximo 280 caracteres.")
        String content) {
}

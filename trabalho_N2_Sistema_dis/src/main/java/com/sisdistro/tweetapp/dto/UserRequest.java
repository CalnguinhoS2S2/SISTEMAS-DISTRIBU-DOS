package com.sisdistro.tweetapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados necessários para criar ou atualizar um usuário.
 */
public record UserRequest(
        @NotBlank(message = "O @handle é obrigatório.")
        @Size(max = 40, message = "O @handle deve ter no máximo 40 caracteres.")
        String handle,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, max = 120, message = "A senha deve ter entre 6 e 120 caracteres.")
        String password,

        @NotBlank(message = "O nome de exibição é obrigatório.")
        @Size(max = 120, message = "O nome de exibição deve ter no máximo 120 caracteres.")
        String displayName,

        @Size(max = 512, message = "A URL da imagem de perfil deve ter no máximo 512 caracteres.")
        String profileImageUrl,

        @Size(max = 512, message = "A biografia deve ter no máximo 512 caracteres.")
        String bio) {
}

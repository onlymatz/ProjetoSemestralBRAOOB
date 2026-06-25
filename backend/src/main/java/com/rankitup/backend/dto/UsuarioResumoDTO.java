package com.rankitup.backend.dto;

import com.rankitup.backend.model.Usuario;
import com.rankitup.backend.model.enums.PerfilUsuario;

public record UsuarioResumoDTO(
        Long idUsuario,
        String email,
        PerfilUsuario perfil
) {
    public static UsuarioResumoDTO from(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioResumoDTO(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }
}

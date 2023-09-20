package com.joabss.financecontrol.service;

import java.util.Optional;

import com.joabss.financecontrol.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);

	Usuario salvarUsuario(Usuario usuario);

	void validarEmail(String email);

	Optional<Usuario> obterPorId(Long id);
}

package com.joabss.minhasfinancas.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joabss.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test // validarEmail
	public void deveVerificarAExistenciaDeUmEmail() {
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		// Ação / execução
		boolean result = repository.existsByEmail("usuario@email.com");

		// Verificação
		Assertions.assertTrue(result);
	}

	@Test // validarEmail
	public void deveRetornarFalsoQuandoNaoHouverUsuarioComEmail() {
		// Cenário

		// Ação / execução
		boolean result = repository.existsByEmail("usuario@email.com");

		// Verificação
		Assertions.assertFalse(result);
	}

	public Usuario autenticar(String email, String senha) {
		// TODO Auto-generated method stub
		return null;
	}

	@Test // salvarUsuario
	public void devePersistirUmUsuarioNaBaseDeDados() {
		// Cenário
		Usuario usuario = criarUsuario();

		// Ação
		Usuario usuarioSalvo = repository.save(usuario);

		// Verificação
		Assertions.assertNotNull(usuarioSalvo.getId());
	}

	@Test // findByEmail
	public void deveBuscarUmUsuarioPorEmail() {
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		// Ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");

		// Verificação
		Assertions.assertTrue(result.isPresent());
	}

	@Test // findByEmail
	public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase() {
		// Cenário

		// Ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");

		// Verificação
		Assertions.assertFalse(result.isPresent());
	}

	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").build();
	}
}

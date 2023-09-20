package com.joabss.financecontrol.model.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joabss.financecontrol.exception.ErroAutenticacao;
import com.joabss.financecontrol.exception.RegraNegocioException;
import com.joabss.financecontrol.model.entity.Usuario;
import com.joabss.financecontrol.model.repository.UsuarioRepository;
import com.joabss.financecontrol.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@Test
	public void deveSalvarUmUsuario() {
		assertDoesNotThrow(() -> {
			// Cenário
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			Usuario usuario = criarusuario();
			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

			// Ação

			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

			// Verificação
			assertNotNull(usuarioSalvo);
			assertEquals(usuarioSalvo.getId(), 1L);
			assertEquals(usuarioSalvo.getNome(), "nome");
			assertEquals(usuarioSalvo.getEmail(), "email@email.com");
			assertEquals(usuarioSalvo.getSenha(), "senha");
		});

	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		// Cenário

		Usuario usuario = criarusuario();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail("email@email.com");

		// Ação
		assertThrows(RegraNegocioException.class, () -> {
			service.salvarUsuario(usuario);
		});

		// Verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		assertDoesNotThrow(() -> {
			// Cenario
			String email = "email@email.com";
			String senha = "senha";

			Usuario usuario = criarusuario();
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

			// Ação
			Usuario result = service.autenticar(email, senha);

			// Verificação
			assertNotNull(result);
		});
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
		// Cenario

		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// Ação
		assertThrows(ErroAutenticacao.class, () -> {
			service.autenticar("email@email.com", "senha");
		});
	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		// Cenario
		Usuario usuario = criarusuario();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// Ação
		assertThrows(ErroAutenticacao.class, () -> {
			service.autenticar("email@email.com", "1234");
		});
	}

	@Test // validarEmail
	public void deveValidarEmail() {
		// Cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// Ação / Execução
		assertDoesNotThrow(() -> {
			service.validarEmail("email@email.com");
		});
	}

	@Test // validarEmail
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		// Cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// Ação
		assertThrows(RegraNegocioException.class, () -> {
			service.validarEmail("email@email.com");
		});
	}

	public static Usuario criarusuario() {
		return Usuario.builder().nome("nome").email("email@email.com").senha("senha").id(1L).build();
	}

}

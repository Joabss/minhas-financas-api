package com.joabss.minhasfinancas.model.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joabss.minhasfinancas.exception.ErroAutenticacao;
import com.joabss.minhasfinancas.exception.RegraNegocioException;
import com.joabss.minhasfinancas.model.entity.Usuario;
import com.joabss.minhasfinancas.model.repository.UsuarioRepository;
import com.joabss.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// Cenário
			Usuario usuario = criarusuario();
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail("email@email.com");

			// Ação
			service.salvarUsuario(usuario);

			// Verificacao
			Mockito.verify(repository, Mockito.never()).save(usuario);
		});
	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertDoesNotThrow(() -> {
			// Cenário
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			Usuario usuario = criarusuario();
			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

			// Ação
			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

			// Verificação
			Assertions.assertNotNull(usuarioSalvo);
			Assertions.assertEquals(usuarioSalvo.getId(), 1L);
			Assertions.assertEquals(usuarioSalvo.getNome(), "nome");
			Assertions.assertEquals(usuarioSalvo.getEmail(), "email@email.com");
			Assertions.assertEquals(usuarioSalvo.getSenha(), "senha");
		});
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			// Cenario
			String email = "email@email.com";
			String senha = "senha";

			Usuario usuario = criarusuario();
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

			// Ação
			Usuario result = service.autenticar(email, senha);

			// Verificação
			Assertions.assertNotNull(result);
		});
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
		Assertions.assertThrows(ErroAutenticacao.class, () -> {
			// Cenario

			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

			// Ação
			service.autenticar("email@email.com", "senha");
		});
	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		Assertions.assertThrows(ErroAutenticacao.class, () -> {
			// Cenario
			Usuario usuario = criarusuario();
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

			// Ação
			service.autenticar("email@email.com", "1234");
		});
	}

	@Test // validarEmail
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			// Cenario
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

			// Ação / Execução
			service.validarEmail("email@email.com");
		});
	}

	@Test // validarEmail
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// Cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

			// Ação
			service.validarEmail("email@email.com");
		});
	}

	public static Usuario criarusuario() {
		return Usuario.builder().nome("nome").email("email@email.com").senha("senha").id(1L).build();
	}

}

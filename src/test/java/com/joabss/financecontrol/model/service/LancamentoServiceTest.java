package com.joabss.financecontrol.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joabss.financecontrol.exception.RegraNegocioException;
import com.joabss.financecontrol.model.entity.Lancamento;
import com.joabss.financecontrol.model.entity.Usuario;
import com.joabss.financecontrol.model.enums.StatusLancamento;
import com.joabss.financecontrol.model.enums.TipoLancamento;
import com.joabss.financecontrol.model.repository.LancamentoRepository;
import com.joabss.financecontrol.model.repository.LancamentoRepositoryTest;
import com.joabss.financecontrol.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		assertDoesNotThrow(() -> {
			// Cenário
			Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
			Mockito.doNothing().when(service).validar(lancamentoASalvar);
			Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
			lancamentoSalvo.setId(1L);
			Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

			// Ação / Execução
			Lancamento lancamento = service.salvar(lancamentoASalvar);

			// Verificação
			assertNotNull(lancamento);
			assertEquals(lancamento.getId(), lancamento.getId());
			assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);

		});
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// Cenário
		Lancamento lancamentoParaSalvar = LancamentoRepositoryTest.criarLancamento();

		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoParaSalvar);
		// Ação
		assertThrows(RegraNegocioException.class, () -> {
			service.salvar(lancamentoParaSalvar);
		});

		// Verificacao
		Mockito.verify(repository, Mockito.never()).save(lancamentoParaSalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		// Cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// Ação / Execução
		assertDoesNotThrow(() -> {
			service.atualizar(lancamentoSalvo);
		});

		// Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarLancamentoNaoSalvo() {
		// Cenário
		Lancamento lancamentoParaAtualizar = LancamentoRepositoryTest.criarLancamento();

		Mockito.doNothing().when(service).validar(lancamentoParaAtualizar);

		Mockito.when(repository.save(lancamentoParaAtualizar)).thenReturn(lancamentoParaAtualizar);

		// Ação / Execução
		assertThrows(NullPointerException.class, () -> {
			service.atualizar(lancamentoParaAtualizar);
		});

		// Verificação
		Mockito.verify(repository, Mockito.never()).save(lancamentoParaAtualizar);

	}

	@Test
	public void deveDeletarUmLancamento() {
		// Cenário
		Lancamento lancamentoParaDeletar = LancamentoRepositoryTest.criarLancamento();
		lancamentoParaDeletar.setId(1L);

		// Ação / Execução
		assertDoesNotThrow(() -> {
			service.deletar(lancamentoParaDeletar);
		});

		// Verificação
		Mockito.verify(repository).delete(lancamentoParaDeletar);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenário
		Lancamento lancamentoParaDeletar = LancamentoRepositoryTest.criarLancamento();

		// Ação / Execução
		assertThrows(NullPointerException.class, () -> {
			service.deletar(lancamentoParaDeletar);
		});

		// Verificação

		Mockito.verify(repository, Mockito.never()).delete(lancamentoParaDeletar);
	}

	@Test
	public void deveFiltrarLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// Ação / Execução
		List<Lancamento> resultado = service.buscar(lancamento);

		// Verificação
		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// Ação / Execução
		service.atualizarStatus(lancamento, novoStatus);

		// Verificação
		assertEquals(lancamento.getStatus(), novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		// Cenário
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// Ação / Execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificação
		assertTrue(resultado.isPresent());
	}

	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		// Cenário
		Long id = 1L;

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Ação / Execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificação
		assertFalse(resultado.isPresent());
	}

	@Test
	public void DeveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();

		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

		lancamento.setDescricao("");

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

		lancamento.setDescricao("Salario");

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

		lancamento.setMes(0);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

		lancamento.setMes(13);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

		lancamento.setMes(1);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

		lancamento.setAno(202);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

		lancamento.setAno(2023);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

		lancamento.setUsuario(new Usuario());

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(1L);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

		lancamento.setValor(BigDecimal.ZERO);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

		lancamento.setValor(BigDecimal.TEN);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lançamento.");

		lancamento.setTipo(TipoLancamento.RECEITA);

	}

	@Test
	public void DeveObterSaldoPorUsuario() {

	}

	@Test
	public void NaoDeveObterSaldoPorUsuario() {

	}
}

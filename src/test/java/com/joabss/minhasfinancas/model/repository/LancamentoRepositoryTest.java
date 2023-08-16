package com.joabss.minhasfinancas.model.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joabss.minhasfinancas.model.entity.Lancamento;
import com.joabss.minhasfinancas.model.enums.StatusLancamento;
import com.joabss.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmLancamento() {
		// Cenário
		Lancamento lancamento = criarLancamento();

		// Ação
		lancamento = repository.save(lancamento);

		// Verificação
		assertNotNull(lancamento);
	}

	@Test
	public void deveDeletarUmUsuario() {
		// Cenário
		Lancamento lancamento = criarEPersistirUmLancamento();

		lancamento = entityManager.find(Lancamento.class, lancamento.getId());

		// Ação
		repository.delete(lancamento);

		// Verificação
		Lancamento lancamentoInexixtente = entityManager.find(Lancamento.class, lancamento.getId());
		assertNull(lancamentoInexixtente);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		// Cenário
		Lancamento lancamento = criarEPersistirUmLancamento();
		lancamento.setAno(2018);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);

		// Ação
		repository.save(lancamento);

		// Verificação
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

		assertEquals(lancamentoAtualizado.getAno(), 2018);
		assertEquals(lancamentoAtualizado.getDescricao(), "Teste Atualizar");
		assertEquals(lancamentoAtualizado.getStatus(), StatusLancamento.CANCELADO);
	}

	@Test
	public void deveBuscarUmLancamentoPorIdobterPorId() {
		// Cenário
		Lancamento lancamento = criarEPersistirUmLancamento();

		// Ação
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

		// Verificação
		assertTrue(lancamentoEncontrado.isPresent());
	}

	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}

	public Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2019)
				.mes(1)
				.descricao("lancamento qualquer")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}
}

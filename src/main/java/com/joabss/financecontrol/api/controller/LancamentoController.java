package com.joabss.financecontrol.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joabss.financecontrol.api.dto.AtualizaStatusDTO;
import com.joabss.financecontrol.api.dto.LancamentoDTO;
import com.joabss.financecontrol.exception.RegraNegocioException;
import com.joabss.financecontrol.model.entity.Lancamento;
import com.joabss.financecontrol.model.entity.Usuario;
import com.joabss.financecontrol.model.enums.StatusLancamento;
import com.joabss.financecontrol.model.enums.TipoLancamento;
import com.joabss.financecontrol.service.LancamentoService;
import com.joabss.financecontrol.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService service;
	private final UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<Object> buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano, 
			@RequestParam("usuario") Long idUsuario
	) {

		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);

		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (usuario.isPresent()) {
			lancamentoFiltro.setUsuario(usuario.get());
		} else {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não econtrado para o Id informado.");
		}

		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}

	@GetMapping("{id}")
	public ResponseEntity<?> obterLancamento(@PathVariable("id") Long id) {
		return service.obterPorId(id)
					.map(lancamento -> new ResponseEntity<Object>(converter(lancamento), HttpStatus.OK))
					.orElseGet(() -> new ResponseEntity<Object>(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	public ResponseEntity<Object> salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity<Object>(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.obterPorId(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> 
			new ResponseEntity<Object>("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity<?> atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		return service.obterPorId(id).map(entity -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

			if (statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
			}

			try {
				service.atualizarStatus(entity, statusSelecionado);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> 
		new ResponseEntity<Object>("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Object> deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entity -> {
			service.deletar(entity);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> 
			new ResponseEntity<Object>("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
	}

	private LancamentoDTO converter(Lancamento lancamento) {
		return LancamentoDTO.builder()
					.id(lancamento.getId())
					.descricao(lancamento.getDescricao())
					.valor(lancamento.getValor())
					.mes(lancamento.getMes())
					.ano(lancamento.getAno())
					.status(lancamento.getStatus().name())
					.tipo(lancamento.getTipo().name())
					.usuario(lancamento.getUsuario().getId())
					.build();
	}

	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService
			.obterPorId(dto.getUsuario())
			.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado."));

		lancamento.setUsuario(usuario);

		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}

		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}

		return lancamento;
	}

}

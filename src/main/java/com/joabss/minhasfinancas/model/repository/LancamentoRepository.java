package com.joabss.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joabss.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}

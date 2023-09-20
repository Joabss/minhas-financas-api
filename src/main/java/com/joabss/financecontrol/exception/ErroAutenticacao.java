package com.joabss.financecontrol.exception;

public class ErroAutenticacao extends RuntimeException{
	private static final long serialVersionUID = -9096251171798262833L;

	public ErroAutenticacao(String msg) {
		super(msg);		
	}
}

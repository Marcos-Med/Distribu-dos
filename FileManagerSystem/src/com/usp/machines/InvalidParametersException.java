package com.usp.machines;

public class InvalidParametersException extends RuntimeException{ //Exceção para tratar parâmetros inválidos
	
	private static final long serialVersionUID = 1L;

	public InvalidParametersException() {
		super("Invalid Parameters!");
	}
}

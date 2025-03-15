package com.usp.machines;

public class InvalidParametersException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public InvalidParametersException() {
		super("Invalid Parameters!");
	}
}

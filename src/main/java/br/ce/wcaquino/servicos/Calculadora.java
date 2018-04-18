package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.DivisionByZeroException;

public class Calculadora {

	public int sum(int i, int j) {
		return i + j;
	}

	public int subtract(int i, int j) {
		return i - j;
	}

	public int divide(int i, int j) throws DivisionByZeroException {
		if(j == 0) throw new DivisionByZeroException();
		return i / j;
	}

}

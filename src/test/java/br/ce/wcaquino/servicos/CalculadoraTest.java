package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.DivisionByZeroException;

public class CalculadoraTest {

	Calculadora calculadora;
	
	@Before
	public void setup() {
		calculadora = new Calculadora();
	}
	
	@Test
	public void somar() {
		Assert.assertEquals(12, calculadora.sum(5, 7));
	}
	
	@Test
	public void subtrair() {
		Assert.assertEquals(3, calculadora.subtract(5, 2));
		Assert.assertEquals(-5, calculadora.subtract(5, 10));
	}
	
	@Test
	public void dividir() throws DivisionByZeroException {
		Assert.assertEquals(2, calculadora.divide(10, 5));
	}
	
	@Test(expected=DivisionByZeroException.class)
	public void dividirPorZero() throws DivisionByZeroException {
		calculadora.divide(1, 0);
	}
	
}

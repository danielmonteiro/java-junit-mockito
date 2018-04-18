package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockSpyTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	public CalculadoraMockSpyTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void deveMostrarDiferencaMockSpy() {
		
		Mockito.when(calcMock.sum(1, 2)).thenReturn(5);
		Mockito.when(calcSpy.sum(1, 2)).thenReturn(5);
		
		System.out.println(calcMock.sum(1, 3));
		System.out.println(calcSpy.sum(1, 3));
	}

}

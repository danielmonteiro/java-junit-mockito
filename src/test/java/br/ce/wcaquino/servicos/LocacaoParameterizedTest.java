package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.dao.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;

@RunWith(Parameterized.class)
public class LocacaoParameterizedTest {
	
	@InjectMocks
	LocacaoService service;
	
	Usuario usuario;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spc;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public double valor;

	@Parameter(value=2)
	public String testName;

	
	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
		usuario = new Usuario("Usuario 1");
	}
	
	private static Filme filme1 = new Filme("Filme 1", 2, 10.0);
	private static Filme filme2 = new Filme("Filme 2", 2, 10.0);
	private static Filme filme3 = new Filme("Filme 3", 2, 10.0);
	private static Filme filme4 = new Filme("Filme 4", 2, 10.0);
	private static Filme filme5 = new Filme("Filme 5", 2, 10.0);
	private static Filme filme6 = new Filme("Filme 6", 2, 10.0);
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParameters() {
		return Arrays.asList(new Object[][] {
			{ Arrays.asList(filme1, filme2), 20, "Alugando 2 filmes: 0% de desconto" },
			{ Arrays.asList(filme1, filme2, filme3), 27.5, "Alugando 3 filmes: 25% de desconto" },
			{ Arrays.asList(filme1, filme2, filme3, filme4), 32.5, "Alugando 4 filmes: 50% de desconto" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 35, "Alugando 5 filmes: 75% de desconto" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 35, "Alugando 6 filmes: 100% de desconto" }
		});
	}

	@Test
	public void aplicarDescontos() throws Exception {
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		Assert.assertThat(locacao.getValor(), is(equalTo(valor)));
		
	}
	
}

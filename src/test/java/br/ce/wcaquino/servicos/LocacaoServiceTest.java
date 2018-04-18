package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.LocacaoBuilder.umaLocacao;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.CaloteiroException;
import br.ce.wcaquino.exceptions.SpcConnectionException;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest {
	
	@InjectMocks
	LocacaoService service;
	Usuario usuario;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		usuario = new Usuario("Usuario 1");
	}
	

	@Test
	public void teste() throws Exception {
		
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario		
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 2, 5.0),
			new Filme("Filme 2", 5, 3.0)
		);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(8.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		//Assert.assertThat(locacao.getDataLocacao(), Matchers.ehHoje());
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected=Exception.class)
	public void alugarFilmeSemEstoque() throws Exception {
		//cenario		
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 2, 5.0),
			new Filme("Filme 2", 0, 3.0)
		);
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	
	@Test
	public void deveDevolverNaSegundaSeAlugarNoSabado() throws Exception {
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(21, 4, 2018));
		
		//cenario		
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 2, 10.0)
		);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
				
		//Assert.assertTrue(DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY));
		Assert.assertThat(locacao.getDataRetorno(), Matchers.ehSegunda());
	}
	
	@Test(expected=CaloteiroException.class)
	public void naoDeveAlugarParaUsuarioNegativado() throws Exception {
		
		//cenario		
		Usuario usuarioCaloteiro = new Usuario("Caloteiro");
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 2, 10.0)
		);
		
		Mockito.when(spc.verificarUsuarioNegativado(usuarioCaloteiro)).thenReturn(true);
		
		//acao
		service.alugarFilme(usuarioCaloteiro, filmes);
				
		
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() throws Exception {
		
		//cenario
		Usuario usuario1 = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Usuario 3").agora();
		
		Mockito.when(dao.getLocacoesPendentes()).thenReturn(
			Arrays.asList(
				umaLocacao().comUsuario(usuario1).comDataLocacao(obterDataComDiferencaDias(-4)).comDataRetorno(DataUtils.obterDataComDiferencaDias(-2)).agora(),
				umaLocacao().comUsuario(usuario2).comDataLocacao(new Date()).comDataRetorno(DataUtils.obterDataComDiferencaDias(1)).agora(),
				umaLocacao().comUsuario(usuario3).comDataLocacao(obterDataComDiferencaDias(-5)).comDataRetorno(DataUtils.obterDataComDiferencaDias(-4)).agora(),
				umaLocacao().comUsuario(usuario3).comDataLocacao(obterDataComDiferencaDias(-5)).comDataRetorno(DataUtils.obterDataComDiferencaDias(-4)).agora()
			)
		);
		
		//acao
		service.notificaLocacoes();
		
		// verificacao
		Mockito.verify(email).notificarAtrasos(usuario1);
		Mockito.verify(email, never()).notificarAtrasos(usuario2);
		Mockito.verify(email, atLeast(2)).notificarAtrasos(usuario3);
	}
	
	@Test(expected=SpcConnectionException.class)
	public void deveTratarErroQuandoConsultaSPC() throws Exception {
		
		//cenario		
		Usuario usuario = new Usuario("Caloteiro");
		List<Filme> filmes = Arrays.asList(
			new Filme("Filme 1", 2, 10.0)
		);
		
		Mockito.when(spc.verificarUsuarioNegativado(usuario)).thenThrow(new SpcConnectionException());
		
		service.alugarFilme(usuario, filmes);
		
	}
	
	@Test
	public void deveProrrogarLocacao() {
		// cenario
		Locacao locacao = LocacaoBuilder.umaLocacao().agora();
		
		// acao
		service.prorrogarLocacao(locacao, 3);
		
		// verificacao
		ArgumentCaptor<Locacao> argCapture = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salva(argCapture.capture());
		
		Locacao locacaoProrogada = argCapture.getValue();
		
		Assert.assertThat(locacaoProrogada.getValor(), is(12.0));
	}
}

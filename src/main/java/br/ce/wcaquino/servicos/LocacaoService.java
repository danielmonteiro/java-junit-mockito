package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.CaloteiroException;
import br.ce.wcaquino.exceptions.SpcConnectionException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO locacaoDAO;
	private SPCService spc;
	private EmailService email;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws Exception {
		
		for(Filme filme : filmes) {
			if(filme.getEstoque() == 0) {
				throw new Exception("Filme sem estoque");
			}
		}
		
		boolean usuarioNegativado = false;
		try{
			usuarioNegativado = spc.verificarUsuarioNegativado(usuario);
		} catch(Exception e) {
			throw new SpcConnectionException();
		}
		
		if(usuarioNegativado) {
			throw new CaloteiroException();
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		double valor = 0;
		int index = 1;
		for(Filme filme : filmes) {
			// Desconto pela quantidade de filme
			switch(index) {
				case(3) :
					// 25% de desconto no 3º filme
					valor += filme.getPrecoLocacao() * 0.75;
					break;
				case(4) :
					// 50% de desconto no 4º filme
					valor += filme.getPrecoLocacao() * 0.5;
					break;
				case(5) :
					// 75% de desconto no 5º filme
					valor += filme.getPrecoLocacao() * 0.25;
					break;
				case(6) :
					// 100% de desconto no 6º filme
					valor += filme.getPrecoLocacao() * 0;
					break;
				default:
					valor += filme.getPrecoLocacao();
			}				
			
			index++;
		}
		locacao.setValor(valor);
		
		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		
		// Se alugar no sabado entrega na segunda
		if(DataUtils.verificarDiaSemana(locacao.getDataLocacao(), Calendar.SATURDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		locacaoDAO.salva(locacao);
		
		return locacao;
	}
	
	public void notificaLocacoes() {
		List<Locacao> locacoes = locacaoDAO.getLocacoesPendentes();
		for(Locacao locacao: locacoes) {
			if(locacao.getDataRetorno().before(new Date())) {
				email.notificarAtrasos(locacao.getUsuario());
			}
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao newLocacao = new Locacao();
		newLocacao.setUsuario(locacao.getUsuario());
		newLocacao.setFilmes(locacao.getFilmes());
		newLocacao.setDataLocacao(new Date());
		newLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		newLocacao.setValor(locacao.getValor() * dias);
		
		locacaoDAO.salva(newLocacao);
	}
	
	
}
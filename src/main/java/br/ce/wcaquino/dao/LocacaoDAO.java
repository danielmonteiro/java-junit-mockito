package br.ce.wcaquino.dao;

import java.util.List;

import br.ce.wcaquino.entidades.Locacao;

public interface LocacaoDAO {

	public void salva(Locacao locacao);

	public List<Locacao> getLocacoesPendentes();
	
}

package br.com.repository;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.entidades.Lancamento;

public interface IDaoLancamento {
	
	
	List<Lancamento> consultar(Long codUser); 
	
	List<SelectItem> listEstados();

}

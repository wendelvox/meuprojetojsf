package br.com.cursojsf;

import org.junit.Test;

import br.com.dao.DaoGeneric;
import br.com.entidades.Pessoa;


public class TesteHibernate {


	@Test
	public void testeHibernate() {
		
		DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
		
		Pessoa pessoa = new Pessoa();
		
		
		pessoa.setNome("Wendel Souza Santos");
		pessoa.setSobrenome("Santos");	
		pessoa.setIdade(40);
		
	
		daoGeneric.salvar(pessoa);
		
	}

}




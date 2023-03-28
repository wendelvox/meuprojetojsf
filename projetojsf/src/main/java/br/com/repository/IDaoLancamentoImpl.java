package br.com.repository;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.entidades.Lancamento;
import br.com.jpautil.JPAUtil;

public class IDaoLancamentoImpl implements IDaoLancamento {

	@Override
	public List<Lancamento> consultar(Long codUser) {

				
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<Lancamento> lista = entityManager.createQuery(" from Lancamento where usuario.id = " + codUser).getResultList();
		
		transaction.commit();
		entityManager.close();
		
		return lista;
	}

	@Override
	public List<SelectItem> listEstados() {
	
		List<SelectItem> selectEstados = new ArrayList<SelectItem>();
		
		EntityManager entityManger = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManger.getTransaction();
		transaction.begin();
		
		List<Estados> estados = entityManger.createQuery("from Estados").getResultList();
		
		for (Estados estado : estados) {
			
			selectEstados.add(new SelectItem(estado, estado.getNome()));			
		}
		
		return selectEstados;
	}

	
	
	
	}


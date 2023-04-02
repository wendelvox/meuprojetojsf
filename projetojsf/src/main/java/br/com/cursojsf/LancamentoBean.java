package br.com.cursojsf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;



import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoLancamento;
import br.com.repository.IDaoLancamentoImpl;

@ViewScoped
@ManagedBean (name = "lancamentoBean")
public class LancamentoBean {
	
	private Lancamento lancamento = new Lancamento();
	private DaoGeneric<Lancamento> dao = new DaoGeneric<Lancamento>();
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	private IDaoLancamento daoLancamento = new IDaoLancamentoImpl();
	
	
	private List<SelectItem> estados;
	private List<SelectItem> cidades;
	
		
	public String novo() {
		lancamento = new Lancamento();
		return "";
	}
	
	public String salvar() {
		FacesContext contex = FacesContext.getCurrentInstance();
		ExternalContext externalContex = contex.getExternalContext();
		HttpServletRequest req = (HttpServletRequest) externalContex.getRequest();
		HttpSession session = req.getSession();		
		Pessoa usuarioLogado = (Pessoa) session.getAttribute("usuarioLogado");
		
		lancamento.setUsuario(usuarioLogado);
		lancamento = dao.merge(lancamento); 
	
		return "";
	}
	
	
	public String remove() {
		
		dao.deletePorId(lancamento);
		lancamento = new Lancamento();	
		carregaLancamento();
		return "";
	}
	
	@PostConstruct
	public void carregaLancamento() {
		FacesContext contex = FacesContext.getCurrentInstance();
		ExternalContext externalContex = contex.getExternalContext();
		HttpServletRequest req = (HttpServletRequest) externalContex.getRequest();
		HttpSession session = req.getSession();		
		Pessoa usuarioLogado = (Pessoa) session.getAttribute("usuarioLogado");
		
      //	lancamentos = daoLancamento.consultar(usuarioLogado.getId());
		
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

	
		lancamentos = dao.getListEntity(Lancamento.class);
	
	}
	
	public void editar() {
		
		if(lancamento.getCidade() != null) {
			
			Estados estados = lancamento.getCidade().getEstados();
			lancamento.setEstado(estados);
			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id= " + estados.getId()).getResultList();
			List<SelectItem> selectItemCidades = new ArrayList<SelectItem>();
			for (Cidades cidade : cidades) {
				selectItemCidades.add(new SelectItem( cidade, cidade.getNome()));
			}
			setCidades(selectItemCidades);

			
		}
	
		
		
	}
	
	public void carregaCidade(AjaxBehaviorEvent event) {
		lancamento = new Lancamento();
		Estados estados = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();
		if(estados != null) {
			lancamento.setEstado(estados);
			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id= " + estados.getId()).getResultList();
			List<SelectItem> selectItemCidades = new ArrayList<SelectItem>();
			for (Cidades cidade : cidades) {
				selectItemCidades.add(new SelectItem( cidade, cidade.getNome()));
			}
			setCidades(selectItemCidades);
		}
		
	}

	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public DaoGeneric<Lancamento> getDao() {
		return dao;
	}

	public void setDao(DaoGeneric<Lancamento> dao) {
		this.dao = dao;
	}

	public List<Lancamento> getLancamentos() {
	

	return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}

	public List<SelectItem> getEstados() {
		
		estados = daoLancamento.listEstados();
		
		return estados;
	}

	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}

	public List<SelectItem> getCidades() {
		return cidades;
	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}



	
	
	
	
	
	

}

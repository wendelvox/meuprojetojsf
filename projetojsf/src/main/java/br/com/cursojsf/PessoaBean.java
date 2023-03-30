package br.com.cursojsf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> dao = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	private List<SelectItem> estados;

	private List<SelectItem> cidades;

	private void mostraMsg(String msg) {
		//
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);

	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/* NOVO */
	public String novo() {
		pessoa = new Pessoa();
		return "";
	}

	/* SALAVAR */
	public String salvar() {
		pessoa = dao.merge(pessoa);
		carregarPessoas();
		mostraMsg("Cadastrado com sucesso");
		return "";
	}

	/* REMOVE */
	public String remove() {

		dao.deletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		return "";
	}

	@PostConstruct
	public void carregarPessoas() {
		pessoas = dao.getListEntity(Pessoa.class);
	}

	public void consultaCEP(AjaxBehaviorEvent event) {

		try {

			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCEP() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String cep = "";
			StringBuilder jsonCEP = new StringBuilder();

			while ((cep = br.readLine()) != null) {

				jsonCEP.append(cep);

			}

			Pessoa gsonAux = new Gson().fromJson(jsonCEP.toString(), Pessoa.class);

			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());

		} catch (Exception ex) {
			// TODO: handle exception
		}

	}

	public String logar() {

		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		if (pessoaUser != null) {

			FacesContext contex = FacesContext.getCurrentInstance();
			ExternalContext externalContex = contex.getExternalContext();
			HttpServletRequest req = (HttpServletRequest) externalContex.getRequest();
			HttpSession session = req.getSession();
			session.setAttribute("usuarioLogado", pessoaUser);
			return "primeirapagina.jsf";
		}
		else {
		     FacesContext.getCurrentInstance().addMessage("msg", new FacesMessage("Usuário não encontrado!"));
		}
		
		return "index.jsf";
	}

	public String deslogar() {
		FacesContext contex = FacesContext.getCurrentInstance();
		ExternalContext externalContex = contex.getExternalContext();
		externalContex.getSessionMap().remove("usuariologado");
		HttpServletRequest req = (HttpServletRequest) externalContex.getRequest();
		req.getSession().invalidate();
		return "index.jsf";

	}

	public boolean permiteAcesso(String acesso) {

		FacesContext contex = FacesContext.getCurrentInstance();
		ExternalContext externalContex = contex.getExternalContext();
		HttpServletRequest req = (HttpServletRequest) externalContex.getRequest();
		HttpSession session = req.getSession();
		Pessoa usuarioLogado = (Pessoa) session.getAttribute("usuarioLogado");

		return usuarioLogado.getPerfil().equals(acesso);

	}

	public List<SelectItem> getEstados() {

		estados = iDaoPessoa.listaEstados();
		return estados;
	}

	public void carregarCidades(AjaxBehaviorEvent event) {

		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();

		if (estado != null) {
			pessoa.setEstados(estado);

			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();

			List<SelectItem> selectItemsCidades = new ArrayList<SelectItem>();

			for (Cidades cidade : cidades) {

				selectItemsCidades.add(new SelectItem(cidade, cidade.getNome()));

			}
			setCidades(selectItemsCidades);
			
		

		}

	}

	public void editar() {

		if (pessoa.getCidades() != null) {
			
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();

			List<SelectItem> selectItemsCidades = new ArrayList<SelectItem>();

			for (Cidades cidade : cidades) {

				selectItemsCidades.add(new SelectItem(cidade, cidade.getNome()));

			}
			setCidades(selectItemsCidades);
			

		}

	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}

	public List<SelectItem> getCidades() {

		return cidades;
	}

}
package curso.api.rest.model;

import java.io.Serializable;
import java.util.List;

public class UsuarioDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userLogin;
	private String userNome;
	private List<Telefones> userTelefones;

	
	public UsuarioDTO(Usuario usuario) {
		super();
		this.userLogin = usuario.getLogin();
		this.userNome = usuario.getNome();
		this.userTelefones = usuario.getTelefones();
	}

	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserNome() {
		return userNome; 
	}
	public void setUserNome(String userNome) {
		this.userNome = userNome;
	}
	public List<Telefones> getUserTelefones() {
		return userTelefones;
	}

	public void setUserTelefones(List<Telefones> userTelefones) {
		this.userTelefones = userTelefones;
	}

}

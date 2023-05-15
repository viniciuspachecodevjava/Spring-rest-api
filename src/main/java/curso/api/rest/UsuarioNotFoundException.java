package curso.api.rest;

public class UsuarioNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsuarioNotFoundException(Long id) {
        super("Usuário não encontrado com o ID: " + id);
    }
}

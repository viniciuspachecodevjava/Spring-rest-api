package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import curso.api.rest.UsuarioNotFoundException;
import curso.api.rest.model.Telefones;
import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.UsuarioRepository;

/* Arquitetura REST */
/*@CrossOrigin(origins = "https://www.urlaqui.com") */
/*A anotacão acima serve para liberar o acesso a um end point caso seja feita uma requisição com ajax,
 * caso contrario ocorre um bloqueio de CORS*/
/*A anotação pode receber um *(Libera todos) ou até um array passando mais parametros*/
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	/* Serviço RESTful */
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id){

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		Usuario usuarioEncontrado = usuario.orElseThrow(() -> new UsuarioNotFoundException(id));
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuarioEncontrado), HttpStatus.OK);

	}

	/* */
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id,
			@PathVariable(value = "venda") Long venda) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);

	}

	/* Resgada da URL */
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	public ResponseEntity<List<UsuarioDTO>> listaUsuarios() {
		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
		List<UsuarioDTO> usuariosDTO = usuarios.stream()
	            .map(usuario -> new UsuarioDTO(usuario))
	            .collect(Collectors.toList());
	    return ResponseEntity.ok().body(usuariosDTO);
	}

	/* Envia por post */
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {

		for (Telefones telefone : usuario.getTelefones()) {
			telefone.setUsuario(usuario);
		}
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		while ((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class );
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		usuario.setComplemento(userAux.getComplemento());

		String encryptedPassword = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(encryptedPassword);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	/* Atualiza */
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody(required = true) Usuario usuario) {

		for (Telefones telefone : usuario.getTelefones()) {
			telefone.setUsuario(usuario);
		}

		Usuario temporaryUser = usuarioRepository.findUserByLogin(usuario.getLogin());

		if (!temporaryUser.getSenha().equals(usuario.getSenha())) {
			
			String encryptedPassword = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(encryptedPassword);
		}
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	/* Deleta */
	@DeleteMapping(value = "/{id}/", produces = "application/json")
	public ResponseEntity<String> deletar(@PathVariable(name = "id") Long id) {

		usuarioRepository.deleteById(id);

		return new ResponseEntity<String>("ok", HttpStatus.OK);
	}

}

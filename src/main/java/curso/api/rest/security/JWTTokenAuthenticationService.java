package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAuthenticationService {

	@Autowired
	private UsuarioRepository repository;

	/* Tempo de expiração do token de 2 dias */
	private static final long EXPIRATION_TIME = 172800000;

	/* Senha única para compor a autenticação e ajudar na segurança */
	private static final String SECRET = "SecretKey";

	/* Prefixo padrão de token */
	private static final String TOKEN_PREFIX = "Bearer";

	/* Retorna no cabeça~ho para identificar a localização do token */
	private static final String HEADER_STRING = "Authorization";

	/*
	 * O método gera o token de autenticação, adiciona ao cabeçalho e produz
	 * resposta para o http
	 */

	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		/* Montagem do token */
		String JWT = Jwts.builder() /* Chama o gerador de token */
				.setSubject(username)/* Define o assunto do JWT, entidade que o JWT está representando */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /* Tempo de expiração do token */
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();/* Compactação e algoritimo de geração de token */
		/* Concatena o token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; /* Bearer 9281931ji123992 */

		/* Adiciona no cabeçalho HTTP */
		response.addHeader(HEADER_STRING, token);

		/* Escreve o token como resposta no corpo */
		response.getWriter().write("{\"Autorization\":\"" + token + "\"}");
	}

	/* Retorna o usuário validado com TOKEN ou se não for válido retorna NULL */

	public Authentication getAuthentication(HttpServletRequest request) throws Exception {
		/* Pega o TOKEN enviado no cabeçalho */
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {
			/* Faz a validação do TOKEN do usuário na requisição */
			String user = Jwts.parser().setSigningKey(SECRET) /* Bearer 9281931ji123992 */
					.parseClaimsJwt(token.replace(TOKEN_PREFIX, "")) /* 9281931ji123992 */
					.getBody().getSubject(); /* João silva */

			if (user != null) {
				Usuario usuario = repository.findUserByLogin(user);

				if (usuario != null) {
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(),
							usuario.getSenha(),
							usuario.getAuthorities());
				}
			}

		}
		return null; /* Não autorizado */
	}
}

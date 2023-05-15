package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.TokenValidationException;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAuthenticationService {

	/* Tempo de expiração do token de 2 dias */
	private static final long EXPIRATION_TIME = 259200000;

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

public void addAuthentication(HttpServletResponse response , String username) throws IOException {
		
		/*Montagem do Token*/
		String JWT = Jwts.builder() /*Chama o gerador de Token*/
				        .setSubject(username) /*Adiciona o usuario*/
				        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*Tempo de expiração*/
				        .signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Compactação e algoritmos de geração de senha*/
		
		/*Junta token com o prefixo*/
		String token = TOKEN_PREFIX + " " + JWT; /*Bearer 87878we8we787w8e78w78e78w7e87w*/
		
		/*Adiciona no cabeçalho http*/
		response.addHeader(HEADER_STRING, token); /*Authorization: Bearer 87878we8we787w8e78w78e78w7e87w*/
		
		corsRelease(response);
		
		/*Escreve token como responsta no corpo http*/
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
		
	}

	/* Retorna o usuário validado com TOKEN ou se não for válido retorna NULL */

	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/* Pega o TOKEN enviado no cabeçalho */
		String token = request.getHeader(HEADER_STRING);
		try {
		if (token != null) {
			String tokenNumber = token.replace(TOKEN_PREFIX, "").trim();
			/* Faz a validação do TOKEN do usuário na requisição */
			String user = Jwts.parser().setSigningKey(SECRET) /*Bearer 87878we8we787w8e78w78e78w7e87w*/
					.parseClaimsJws(tokenNumber) /*87878we8we787w8e78w78e78w7e87w*/
					.getBody().getSubject(); /*João Silva*/

			if (user != null) {
				
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
				        .getBean(UsuarioRepository.class).findUserByLogin(user);

				if (usuario != null) {
					if (tokenNumber.equalsIgnoreCase(usuario.getToken())) {
						
						return new UsernamePasswordAuthenticationToken(
								usuario.getLogin(),
								usuario.getSenha(),
								usuario.getAuthorities());
					}
				}
			}

		}
		}catch (io.jsonwebtoken.ExpiredJwtException e) {
			response.getOutputStream().print("Token expirado: ");
			 throw new TokenValidationException("Token expirado");
		}
		corsRelease(response);
		return null; /* Não autorizado */
	}

	private void corsRelease(HttpServletResponse response) {

		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
		
	}
}

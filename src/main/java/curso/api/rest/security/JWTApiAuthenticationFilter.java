package curso.api.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JWTApiAuthenticationFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/* Estabelece a autenticação para a requisição */

		try {
			Authentication authentication = new JWTTokenAuthenticationService()
					.getAuthentication((HttpServletRequest) request);
			/* Coloca o processo de autenticação no spring security */
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*Continua o processo*/
		chain.doFilter(request, response);
	}

}

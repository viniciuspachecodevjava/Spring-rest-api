package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

/*Mapeia URL, endereços, autoriza ou bloqueia acessos a URL*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	/* Configura as solicitações de acesso HTTP */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/* Ativando a proteção contra usuários que não estão validados por TOKEN */
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

				/*
				 * Ativando a permissão para acesso a página inicial do sistema EX:
				 * sistema.com/index ou sistema.com/
				 */
				.disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/index").permitAll()

				/* URL de logout - Redireciona após o user deslogar do sistema */
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

				/* Mapeia URL de logout e invalida usuário */
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/* Serviço que irá consultar o usuário no banco de dados */
		auth.userDetailsService(implementacaoUserDetailsService)

				/* Padrão de codificação de senha */
				.passwordEncoder(new BCryptPasswordEncoder());
	}
}

package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebMvc
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Override // configura as solicitações de acessos por http
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() // Desativa as configurações padrão de memória do Spring
				.authorizeRequests() // Permitir restrigir acessos
				.antMatchers(HttpMethod.GET, "/").permitAll() // Qualquer usuário acessa a página inicial
			//	.antMatchers("/materialize/**").permitAll()
				.antMatchers(HttpMethod.GET, "**/cadastropessoa").hasAnyRole("ADMIN")
				.anyRequest().authenticated().and()
				.formLogin().permitAll() // permite qualquer usuário
				.loginPage("/login")
				.defaultSuccessUrl("/cadastropessoa")
				.failureUrl("/login?error=true")
				.and().logout()
				.logoutSuccessUrl("/login")
				//Mapeia URL de Logout e invalida Usuário autenticado.
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}

	@Override // Cria a autenticação do Usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
//		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder()).withUser("Alex")
//				.password("$2a$10$HwJ6SKh1SgiNdc.eAHazge0tUrZG68Cawpqq/rMRdIdqQkijuA8L.").roles("ADMIN");
	}

	@Override // ignora URL específicas
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
	}

}

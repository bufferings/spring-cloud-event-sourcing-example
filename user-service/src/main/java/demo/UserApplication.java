package demo;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * The {@link UserApplication} is a cloud-native Spring Boot application that
 * manages a bounded context for @{link User}, @{link Account}, @{link
 * CreditCard}, and @{link Address}
 *
 * @author Kenny Bastani
 * @author Josh Long
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableEurekaClient
@EnableHystrix
@EnableResourceServer
@EnableAuthorizationServer
@RestController
public class UserApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
    
	@Component
	public static class CustomizedRestMvcConfiguration extends RepositoryRestConfigurerAdapter {

		@Override
		public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
			config.setBasePath("/api");
		}
	}

	@Configuration
	public static class WebMvcConfig extends WebMvcConfigurerAdapter {

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/login").setViewName("login");
		}
	}

	@Configuration
	@Order(-5)
	public static class AuthorizationServerConfig extends WebSecurityConfigurerAdapter {
	
	    @Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
	    }

	    @Override
	    public void configure(WebSecurity web) throws Exception {
	        web.ignoring().antMatchers("/resources/**");
	    }
	
	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	    	http.requestMatchers()
                .antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access");
	        http.authorizeRequests()
	            .anyRequest().authenticated();
            http.formLogin().loginPage("/login").permitAll();
	        http.csrf().disable();
	        
	        // ここをプロテクトして、ログイン画面にリダイレクトする。
	        // FBログインが終わったら、ここに戻ってきて、そうしたらauthorizeとかにアクセスできる。
//	        http.antMatcher("/**")                                       
//	        .authorizeRequests()
//	          .antMatchers("/", "/login**", "/webjars/**").permitAll() 
//	          .anyRequest().authenticated()                            
//	        .and().exceptionHandling()
//	          .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"));
	        // login/fb,ghエンドポイントは、FBとかにログインするために使う
        }
	
	}

}

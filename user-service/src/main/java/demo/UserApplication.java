package demo;

import java.security.Principal;
import java.util.Optional;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.user.User;
import demo.user.UserRepository;

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
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	RequestDumperFilter requestDumperFilter() {
		return new RequestDumperFilter();
	}

	@RestController
	public class UserController {

		private UserRepository userRepository;
		
	    @Autowired
	    public UserController(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    @RequestMapping(path = "/me")
	    public ResponseEntity<User> me(Principal principal) {
	        User user = null;
	        if(principal != null) {
	            user = userRepository.findUserByUsername(principal.getName());
	        }

	        return Optional.ofNullable(user)
	                .map(a -> new ResponseEntity<User>(a, HttpStatus.OK))
	                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
	    }
	}

}

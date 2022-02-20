package account.config;

import account.model.user.Role;
import account.service.UserService;
import account.util.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity(debug = true)
@AllArgsConstructor
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final RestAuthEntryPoint restAuthEntryPoint;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(restAuthEntryPoint)
                .and()
                .csrf()
                .disable()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .authorizeRequests()
                // AdminController
                .antMatchers("/api/admin/**")
                .hasAnyRole(Role.ADMINISTRATOR.name())
                // AuthController
                .antMatchers(HttpMethod.POST, "/api/auth/signup")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/changepass")
                .hasAnyRole(Role.USER.name(), Role.ACCOUNTANT.name(), Role.ADMINISTRATOR.name())
                // BusinessController
                .antMatchers(HttpMethod.GET, "/api/empl/payment")
                .hasAnyRole(Role.USER.name(), Role.ACCOUNTANT.name())
                .antMatchers(HttpMethod.POST, "/api/acct/payments")
                .hasAnyRole(Role.ACCOUNTANT.name())
                .antMatchers(HttpMethod.PUT, "/api/acct/payments")
                .hasAnyRole(Role.ACCOUNTANT.name())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) ->
                response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
    }

}

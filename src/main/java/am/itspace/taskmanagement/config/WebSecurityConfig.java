package am.itspace.taskmanagement.config;

import am.itspace.taskmanagement.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin()
                .loginPage("/loginPage")
                .loginProcessingUrl("/login")
                .failureUrl("/loginPage?error=true")
                .defaultSuccessUrl("/loginSuccess")
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .authorizeRequests()
                .antMatchers("/tasks/add").hasAuthority(Role.MANAGER.name())
                .antMatchers("/tasks").authenticated()
                .antMatchers("/users").hasAuthority(Role.MANAGER.name())
                .antMatchers("/users/delete").hasAuthority(Role.MANAGER.name())
                .antMatchers("/manager").hasAuthority(Role.MANAGER.name())
                .antMatchers("/user").hasAuthority(Role.USER.name())
                .anyRequest()
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/accessDenied");
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder);
    }
}
package com.project.medtech.config;

import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.jwt.JwtFilter;
import com.project.medtech.model.AppointmentType;
import com.project.medtech.model.User;
import com.project.medtech.repository.AppointmentTypeRepository;
import com.project.medtech.repository.UserRepository;
import com.project.medtech.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final UserRepository userRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("#{'${appointments.list}'.split(',')}")
    private List<String> appointments;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }

    @Bean
    public void configure() {

        User user = userRepository.findByEmail("trustmed.team3@gmail.com");

        if (user == null) {
            User superAdmin = new User();
            superAdmin.setEmail("trustmed.team3@gmail.com");
            superAdmin.setPassword("$2a$12$UNNiXe1QGTWoyzJ.U13o.OUNbhXu1ejDsflbK0EwCajpPgn3inD/a");
            superAdmin.setFirstName("Neobis");
            superAdmin.setLastName("Team");
            superAdmin.setMiddleName("Four");
            superAdmin.setPhoneNumber("");
            superAdmin.setOtpUsed(true);
            superAdmin.setRole(Role.SUPERADMIN);
            superAdmin.setStatus(Status.ACTIVE);

            userRepository.save(superAdmin);
        }

        ArrayList<AppointmentType> appointmentTypes = (ArrayList<AppointmentType>) appointmentTypeRepository.findAll();
        ArrayList<String> appointmentTypesNames =
                (ArrayList<String>) appointmentTypes.stream().map(AppointmentType::getName).collect(Collectors.toList());
        ArrayList<String> list = (ArrayList<String>) appointments;
        for(String s: list) {
            if(!appointmentTypesNames.contains(s)) {
                AppointmentType appointmentType = new AppointmentType();
                appointmentType.setName(s);
                appointmentTypeRepository.save(appointmentType);
            }
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}

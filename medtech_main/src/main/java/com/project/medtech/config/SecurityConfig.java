package com.project.medtech.config;

import com.project.medtech.dto.enums.AppointmentEnum;
import com.project.medtech.dto.enums.DefaultImageUrl;
import com.project.medtech.dto.enums.Role;
import com.project.medtech.dto.enums.Status;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.jwt.JwtFilter;
import com.project.medtech.model.AppointmentTypeEntity;
import com.project.medtech.model.ContentEntity;
import com.project.medtech.model.RoleEntity;
import com.project.medtech.model.UserEntity;
import com.project.medtech.repository.ContentRepository;
import com.project.medtech.repository.AppointmentTypeRepository;
import com.project.medtech.repository.RoleRepository;
import com.project.medtech.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final RoleRepository roleRepository;

    private final ContentRepository contentRepository;


    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;


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
    @Transactional
    public void configure() {
        ArrayList<AppointmentTypeEntity> appointmentTypeEntities = (ArrayList<AppointmentTypeEntity>) appointmentTypeRepository.findAll();

        ArrayList<String> appointmentTypesNames =
                (ArrayList<String>) appointmentTypeEntities.stream().map(AppointmentTypeEntity::getName).collect(Collectors.toList());

        List<AppointmentEnum> list = AppointmentEnum.getAppointments();

        for (AppointmentEnum s : list) {
            if (!appointmentTypesNames.contains(s.name())) {
                AppointmentTypeEntity appointmentTypeEntity = new AppointmentTypeEntity();
                appointmentTypeEntity.setName(s.name());
                appointmentTypeRepository.save(appointmentTypeEntity);
            }
        }

        Role[] roleArray = Role.values();

        for (Role r : roleArray) {
            Optional<RoleEntity> role = roleRepository.findByName(r.name());
            if (!role.isPresent()) {
                RoleEntity roleEntity = new RoleEntity();

                roleEntity.setName(r.name());

                roleRepository.save(roleEntity);
            }
        }

        UserEntity user = userRepository.findByEmail("tilekju@gmail.com");

        if (user == null) {
            UserEntity superAdmin = new UserEntity();
            superAdmin.setEmail("tilekju@gmail.com");
            superAdmin.setPassword("$2a$12$UNNiXe1QGTWoyzJ.U13o.OUNbhXu1ejDsflbK0EwCajpPgn3inD/a");
            superAdmin.setFirstName("Neobis");
            superAdmin.setLastName("Team");
            superAdmin.setMiddleName("Four");
            superAdmin.setPhoneNumber("");
            superAdmin.setOtpUsed(true);
            RoleEntity roleEntity = roleRepository.findByName("SUPERADMIN")
                    .orElseThrow(
                            () -> new ResourceNotFoundException("No role was found with name: SUPERADMIN")
                    );
            superAdmin.setRoleEntity(roleEntity);
            superAdmin.setStatus(Status.ACTIVE);

            userRepository.save(superAdmin);
        }

        List<ContentEntity> contentEntities = contentRepository.findAll();

        if (contentEntities.isEmpty()) {
            for (int i = 1; i < 43; i++) {
                for (int k = 1; k < 4; k++) {
                    contentRepository.save(createDefaultEntity(i, k));
                }
            }
        } else {
            for (int i = 1; i < 43; i++) {
                for (int k = 1; k < 4; k++) {
                    ContentEntity findEntityByWeekAndOrder =
                            contentRepository.findByWeekNumberAndOrder(i, k);
                    if (findEntityByWeekAndOrder == null) {
                        contentRepository.save(createDefaultEntity(i, k));
                    }
                }
            }
        }

    }

    public ContentEntity createDefaultEntity(Integer week, Integer order) {
        ContentEntity entity = new ContentEntity();

        entity.setWeekNumber(week);
        entity.setOrder(order);
        entity.setHeader("");
        entity.setSubtitle("");
        entity.setDescription("");
        entity.setImageUrl(DefaultImageUrl.DEFAULT_IMAGE_ONE.getUrl());

        return entity;
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

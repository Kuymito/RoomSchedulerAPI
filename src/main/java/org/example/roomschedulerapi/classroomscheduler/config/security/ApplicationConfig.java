package org.example.roomschedulerapi.classroomscheduler.config.security;

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository; // ✅ Import AdminRepository
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.service.impl.CustomUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AdminRepository adminRepository;         // ✅ Inject AdminRepository
    private final InstructorRepository instructorRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        // ✅ Return an instance of your new custom service
        return new CustomUserDetailsServiceImpl(adminRepository, instructorRepository);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // This will now use your custom service
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
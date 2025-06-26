package org.example.roomschedulerapi.classroomscheduler.service.impl; // Or a dedicated 'security' sub-package

import lombok.RequiredArgsConstructor;
import org.example.roomschedulerapi.classroomscheduler.repository.AdminRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final InstructorRepository instructorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. First, try to find the user in the Admin table
        UserDetails user = adminRepository.findByEmail(email).orElse(null);

        // 2. If not found, try to find the user in the Instructor table
        if (user == null) {
            user = instructorRepository.findByEmail(email).orElse(null);
        }

        // 3. If still not found in either table, throw an exception
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return user;
    }
}
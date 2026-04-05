package com.example.attendance.service;

import com.example.attendance.model.Faculty;
import com.example.attendance.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Faculty faculty = facultyRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = faculty.getRole(); // e.g. ADMIN or FACULTY
        if ("END_USER".equalsIgnoreCase(role)) {
            role = "FACULTY";
        }
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        return new User(faculty.getUsername(), faculty.getPassword(), Collections.singleton(authority));
    }
}


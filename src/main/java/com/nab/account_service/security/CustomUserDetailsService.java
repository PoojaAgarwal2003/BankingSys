package com.nab.account_service.security;

import com.nab.account_service.model.User;
import com.nab.account_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));

    return new org.springframework.security.core.userdetails.User(
        user.getUserId(),
        user.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    );
    }
}

package com.engagetech.security.service;

import com.engagetech.security.domain.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final UserRepository userRepository;

    public AuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        com.engagetech.security.domain.model.User user = userRepository.findOneByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User does not exist");
        }
        return user;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("Invalid username/password.");
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!Objects.equals(userDetails.getPassword(), presentedPassword)) {
            throw new BadCredentialsException("Invalid username/password");
        }
    }
}

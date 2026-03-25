/*
 * Loads authenticated users by username or email for Spring Security authentication flows.
 */
package bw.org.bocra.api.security;

import bw.org.bocra.api.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmailOrUsername(username)
                .map(SecurityUser::from)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}

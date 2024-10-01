package net.sparkminds.ekyc.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.ekyc.entity.User;
import net.sparkminds.ekyc.exception.NotFoundException;
import net.sparkminds.ekyc.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumberOrEmail(emailOrPhoneNumber, emailOrPhoneNumber)
                .orElseThrow(() -> new NotFoundException("user.not.found"));
        return UserDetailsImpl.build(user);
    }

}

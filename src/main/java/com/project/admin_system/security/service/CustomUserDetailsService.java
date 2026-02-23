package com.project.admin_system.security.service;

import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.security.dto.AccountContext;
import com.project.admin_system.security.dto.AccountDto;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailId(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getRoleKey()));

        return new AccountContext(AccountDto.from(user), authorities, user.getPassword());
    }
}

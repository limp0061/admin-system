package com.project.admin_system.security.dto;

import com.project.admin_system.user.domain.UserStatus;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AccountContext implements UserDetails, CredentialsContainer {

    private final AccountDto accountDto;
    private final List<GrantedAuthority> authorities;
    private String password;

    public AccountContext(AccountDto accountDto, List<GrantedAuthority> authorities, String password) {
        this.accountDto = accountDto;
        this.authorities = authorities;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return accountDto.emailId();
    }

    // 인증 완 후 비밀번호 null
    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 (로그인 안 한 지 1년 등) ex) lastLogin
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountDto.userStatus() != UserStatus.LOCKED && accountDto.pwFailCount() < 5;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 예: 마지막 비번 변경일이 90일 이내인지 체크하는 로직이 있으면 넣었다면 여기서 사용
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountDto.userStatus() == UserStatus.ACTIVE || accountDto.userStatus() == UserStatus.LOCKED;
    }
}

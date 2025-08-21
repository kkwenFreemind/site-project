package com.alertsystem.security;

import com.alertsystem.entity.User;
import com.alertsystem.repository.MenuRepository;
import com.alertsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> new UsernameNotFoundException("用戶不存在: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 添加角色權限
        authorities.addAll(user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList()));
        
        // 添加選單權限
        List<String> permissions = menuRepository.findPermissionsByUserId(user.getId());
        authorities.addAll(permissions.stream()
            .filter(permission -> permission != null && !permission.trim().isEmpty())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList()));

        return UserPrincipal.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .password(user.getPasswordHash())
            .authorities(authorities)
            .enabled(user.getStatus() == User.UserStatus.ACTIVE)
            .build();
    }
}

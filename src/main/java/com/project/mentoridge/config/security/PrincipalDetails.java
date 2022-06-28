package com.project.mentoridge.config.security;

import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails {

    private User user;
    private Map<String, Object> attributes;
    private List<GrantedAuthority> authorities = new ArrayList<>();

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user, String roleType) {
        this.user = user;
        setAuthority(roleType);
    }

    public PrincipalDetails(User user, Map<String, Object> attributes, List<GrantedAuthority> authorities) {
        this.user = user;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    public String getAuthority() {
        if (this.authorities.isEmpty()) {
            return RoleType.MENTEE.getType();
        }
        return this.authorities.get(0).getAuthority();
    }

    public void setAuthority(String roleType) {
        authorities.add((GrantedAuthority) () -> roleType);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//
//        authorities.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                return RoleType.MENTEE.getType();
//                // return user.getRole().getType();
//            }
//        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return (!user.isDeleted()) && user.isEmailVerified();
    }
/*
    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public <A> A getAttribute(String name) {
        return (A) attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }*/
}

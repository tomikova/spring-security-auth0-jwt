package example.auth.client.security.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class ClientUserDetails implements UserDetails {

    Collection<? extends GrantedAuthority> authorities
    String password
    String username
    Boolean accountNonExpired
    Boolean accountNonLocked
    Boolean credentialsNonExpired
    Boolean enabled

    ClientUserDetails(String username, Collection authorities = [], String password = "", Boolean accountNonExpired = true,
                      Boolean accountNonLocked = true, Boolean credentialsNonExpired = true, Boolean enabled = true) {
        this.username = username
        this.authorities = authorities
        this.password = password
        this.accountNonExpired = accountNonExpired
        this.accountNonLocked = accountNonLocked
        this.credentialsNonExpired = credentialsNonExpired
        this.enabled = enabled
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        authorities
    }

    @Override
    String getPassword() {
        password
    }

    @Override
    String getUsername() {
        username
    }

    @Override
    boolean isAccountNonExpired() {
        accountNonExpired
    }

    @Override
    boolean isAccountNonLocked() {
        accountNonLocked
    }

    @Override
    boolean isCredentialsNonExpired() {
        credentialsNonExpired
    }

    @Override
    boolean isEnabled() {
        enabled
    }
}

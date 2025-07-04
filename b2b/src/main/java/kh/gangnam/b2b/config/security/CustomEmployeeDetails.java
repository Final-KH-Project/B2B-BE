package kh.gangnam.b2b.config.security;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomEmployeeDetails implements UserDetails, Principal {

    @Getter
    private final Long employeeId;
    private final String loginId;
    private final String password;
    private final String name;
    @Getter
    private final String role;

    public CustomEmployeeDetails(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.loginId = employee.getLoginId();
        this.password = employee.getPassword();
        this.name = employee.getName();
        this.role = employee.getRole();
    }

    public CustomEmployeeDetails(Long employeeId, String loginId, String name, String role) {
        this.employeeId = employeeId;
        this.loginId = loginId;
        this.role = role;
        this.name = name;
        // refresh 토큰 갱신 시에는 비밀번호 필요 없음
        this.password = "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return role;
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {

        return password;
    }

    @Override
    public String getUsername() {

        return loginId;
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

        return true;
    }

    @Override
    public String getName() {
        return loginId;
    }
    public String getRealName() {
        return name;
    }
}

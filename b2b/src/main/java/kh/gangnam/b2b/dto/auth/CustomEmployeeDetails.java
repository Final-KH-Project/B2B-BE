package kh.gangnam.b2b.dto.auth;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomEmployeeDetails implements UserDetails, Principal {

    private final Employee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return employee.getRole();
            }
        });

        return collection;
    }
    public Employee getEmployee(){
        return employee;
    }

    @Override
    public String getPassword() {

        return employee.getPassword();
    }

    @Override
    public String getUsername() {

        return employee.getLoginId();
    }

    public Long getEmployeeId() {
        return employee.getEmployeeId();
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

    public String getName() {
        // Principal의 getName() 구현 (보통 username 반환)
        return employee.getName();
    }
}

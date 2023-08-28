package com.example.airportv3.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "user_password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Column(name = "is_enabled")
    private Boolean isEnabled;
    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "id")
    private PositionEntity userPosition;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<RoleEntity> roles;

//    @OneToMany(mappedBy = "applicationUsersEntity")
//    private List<ClientReviewsEntity> userFlightsRegistartionsList;
    @OneToOne(mappedBy = "servicedBy") //cascade = CascadeType.MERGE
    private AircraftEntity servicedAircraft;

    @PrePersist
    private void prePersist() {
        this.isEnabled = Boolean.TRUE;
        this.registeredAt = LocalDateTime.now();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles();
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
    public String getUsername() {
        return this.username;
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }
}

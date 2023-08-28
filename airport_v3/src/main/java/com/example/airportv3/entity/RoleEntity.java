package com.example.airportv3.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "app_roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class RoleEntity implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "role_title")
    private String roleTitle;

    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "id")
    private PositionEntity userPositions;

    public String getRoleTitle() {
        return roleTitle;
    }

    @Override
    public String getAuthority() {
        return  "ROLE_" + this.getRoleTitle();
    }
}

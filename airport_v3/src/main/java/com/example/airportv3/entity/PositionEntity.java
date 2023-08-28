package com.example.airportv3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_positions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "position_title")
    private String positionTitle;

    @OneToMany(mappedBy = "userPositions")
    private List<RoleEntity> userRolesEntityList;
    @OneToMany(mappedBy = "userPosition")
    private List<UserEntity> applicationUsersEntityList;

}

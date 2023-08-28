package com.example.airportv3.util;

import com.example.airportv3.entity.RoleEntity;

import java.util.List;
import java.util.Objects;

public class RoleUtil {
    public static boolean checkWhetherTheListOfUserRolesContainsSuchUserRoleName(
            List<RoleEntity> roleEntities,
            String userRoleTitle
    ){
        if(Objects.isNull(roleEntities) || roleEntities.isEmpty()) {
            throw new IllegalArgumentException("Список ролей пользователя не может быть null или пустым!");
        }
        if(Objects.isNull(userRoleTitle) || userRoleTitle.isEmpty()) {
            throw new IllegalArgumentException("Проверяемая роль пользователя не может быть null или пустой!");
        }
        for (RoleEntity role : roleEntities) {
            if(role.getRoleTitle().equals(userRoleTitle)) {
                return true;
            }
        }
        return false;
    }
}

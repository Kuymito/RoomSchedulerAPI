package org.example.roomschedulerapi.classroomscheduler.model.dto; // Adjust to your package

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.management.relation.Role;

@Getter
@Setter
public class RoleResponseDto {
    private Long roleId;
    private String roleName;

    public RoleResponseDto(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public RoleResponseDto() {}

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
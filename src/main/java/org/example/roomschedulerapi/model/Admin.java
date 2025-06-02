// FILE: org/example/mybatisdemo/model/Admin.java
package org.example.roomschedulerapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Admin {
    @Schema(description = "Unique identifier of the Admin record", example = "1")
    private Integer adminId;

    @Schema(description = "User ID associated with this admin", example = "201", required = true)
    private Integer userId; // FK to users(user_id)

    public Admin() {
    }

    // Constructor, Getters, and Setters
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
package org.example.roomschedulerapi.classroomscheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Creates a constructor that accepts all fields (in this case, just the token)
public class AuthResponseDto {

    private String token;

}
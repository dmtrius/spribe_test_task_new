package pl.spribe.dto;

import lombok.Data;
import pl.spribe.entity.User;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private User.Role role;
    private LocalDateTime createdAt;
}

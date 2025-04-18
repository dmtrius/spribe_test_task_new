package pl.spribe.dto;

import lombok.Data;
import pl.spribe.entity.User;

@Data
public class UserCreateRequestDTO {
    private String username;
    private String fullName;
    private User.Role role;
}

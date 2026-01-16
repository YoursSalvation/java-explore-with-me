package main.server.mapper;

import lombok.experimental.UtilityClass;
import main.dto.NewUserRequest;
import main.dto.UserDto;
import main.dto.UserShortDto;
import main.server.model.User;

@UtilityClass
public class UserMapper {

    public User toEntity(NewUserRequest dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserShortDto toShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
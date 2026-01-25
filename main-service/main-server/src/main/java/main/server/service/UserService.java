package main.server.service;

import main.dto.NewUserRequest;
import main.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest request);

    List<UserDto> getAll(List<Long> ids, int from, int size);

    void delete(Long userId);
}
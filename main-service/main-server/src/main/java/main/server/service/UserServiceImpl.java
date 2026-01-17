package main.server.service;

import lombok.RequiredArgsConstructor;
import main.dto.NewUserRequest;
import main.dto.UserDto;
import main.server.mapper.UserMapper;
import main.server.model.User;
import main.server.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto create(NewUserRequest request) {
        User user = repository.save(UserMapper.toEntity(request));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<User> page = (ids == null || ids.isEmpty())
                ? repository.findAll(pageable)
                : repository.findAllByIdIn(ids, pageable);

        return page.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long userId) {
        repository.deleteById(userId);
    }
}
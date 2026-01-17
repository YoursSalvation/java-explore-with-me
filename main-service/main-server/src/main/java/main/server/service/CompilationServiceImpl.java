package main.server.service;

import lombok.RequiredArgsConstructor;
import main.dto.CompilationDto;
import main.dto.NewCompilationDto;
import main.server.exception.NotFoundException;
import main.server.mapper.CompilationMapper;
import main.server.model.Compilation;
import main.server.repository.CompilationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;

    @Override
    public CompilationDto create(NewCompilationDto dto) {
        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .build();

        return CompilationMapper.toDto(repository.save(compilation));
    }

    @Override
    public void delete(Long compId) {
        repository.deleteById(compId);
    }

    @Override
    public CompilationDto update(Long compId, NewCompilationDto dto) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        compilation.setTitle(dto.getTitle());
        compilation.setPinned(
                dto.getPinned() != null ? dto.getPinned() : compilation.getPinned()
        );

        return CompilationMapper.toDto(repository.save(compilation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Compilation> page = pinned == null
                ? repository.findAll(pageable)
                : repository.findAllByPinned(pinned, pageable);

        return page.stream()
                .map(CompilationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        return repository.findById(compId)
                .map(CompilationMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
    }
}
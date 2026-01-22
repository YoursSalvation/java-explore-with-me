package main.server.service;

import lombok.RequiredArgsConstructor;
import main.dto.CompilationDto;
import main.dto.NewCompilationDto;
import main.server.exception.NotFoundException;
import main.server.mapper.CompilationMapper;
import main.server.model.Compilation;
import main.server.model.Event;
import main.server.repository.CompilationRepository;
import main.server.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto create(NewCompilationDto dto) {
        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .events(new HashSet<>())
                .build();

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Event> events = eventRepository.findAllById(dto.getEvents())
                    .stream()
                    .collect(Collectors.toSet());
            compilation.setEvents(events);
        }

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
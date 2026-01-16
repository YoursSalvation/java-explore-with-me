package main.server.mapper;

import lombok.experimental.UtilityClass;
import main.dto.CompilationDto;
import main.dto.EventShortDto;
import main.server.model.Compilation;

import java.util.List;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(e -> EventMapper.toShortDto(e, 0))
                .toList();

        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                events
        );
    }
}
package main.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.dto.CompilationDto;
import main.dto.NewCompilationDto;
import main.server.service.CompilationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        service.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Long compId,
                                 @RequestBody @Valid NewCompilationDto dto) {
        return service.update(compId, dto);
    }
}
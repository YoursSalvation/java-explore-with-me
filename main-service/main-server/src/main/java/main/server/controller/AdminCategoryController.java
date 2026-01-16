package main.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.dto.CategoryDto;
import main.dto.NewCategoryDto;
import main.server.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @RequestBody @Valid NewCategoryDto dto) {
        return service.update(catId, dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        service.delete(catId);
    }
}
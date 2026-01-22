package main.server.service;

import lombok.RequiredArgsConstructor;
import main.dto.CategoryDto;
import main.dto.NewCategoryDto;
import main.server.exception.ConflictException;
import main.server.exception.NotFoundException;
import main.server.mapper.CategoryMapper;
import main.server.model.Category;
import main.server.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public CategoryDto create(NewCategoryDto dto) {
        try {
            Category category = repository.saveAndFlush(
                    CategoryMapper.toEntity(dto)
            );
            return CategoryMapper.toDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Category with name=" + dto.getName() + " already exists"
            );
        }
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto dto) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        try {
            category.setName(dto.getName());
            return CategoryMapper.toDto(repository.saveAndFlush(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Category with name=" + dto.getName() + " already exists"
            );
        }
    }

    @Override
    public void delete(Long catId) {
        repository.deleteById(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return repository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long catId) {
        return repository.findById(catId)
                .map(CategoryMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }
}
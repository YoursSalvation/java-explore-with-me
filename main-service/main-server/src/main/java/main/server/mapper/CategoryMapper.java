package main.server.mapper;

import lombok.experimental.UtilityClass;
import main.dto.CategoryDto;
import main.dto.NewCategoryDto;
import main.server.model.Category;

@UtilityClass
public class CategoryMapper {

    public Category toEntity(NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    public CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
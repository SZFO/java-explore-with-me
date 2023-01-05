package ru.practicum.ewmmain.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(Long categoryId);

    void delete(Long categoryId);
}
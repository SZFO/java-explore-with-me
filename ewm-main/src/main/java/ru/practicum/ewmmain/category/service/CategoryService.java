package ru.practicum.ewmmain.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    List<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);
}
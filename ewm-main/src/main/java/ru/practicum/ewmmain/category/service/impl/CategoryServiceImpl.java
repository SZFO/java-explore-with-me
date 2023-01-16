package ru.practicum.ewmmain.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;
import ru.practicum.ewmmain.category.mapper.CategoryMapper;
import ru.practicum.ewmmain.category.model.Category;
import ru.practicum.ewmmain.category.repository.CategoryRepository;
import ru.practicum.ewmmain.category.service.CategoryService;
import ru.practicum.ewmmain.exception.ConflictException;
import ru.practicum.ewmmain.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        throwExistName(newCategoryDto.getName());
        Category category = categoryMapper.dtoNewToCategory(newCategoryDto);
        category = categoryRepository.save(category);
        log.info("Создана категория: {}.", category);

        return categoryMapper.categoryToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        throwExistName(categoryDto.getName());
        Category category = categoryMapper.dtoToCategory(categoryDto);
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.debug("Категория с id={} обновлена.", category.getId());

        return categoryMapper.categoryToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(Pageable pageable) {
        List<CategoryDto> categoryDtos = categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::categoryToDto)
                .collect(Collectors.toList());
        log.info("Получение категориий: {}.", categoryDtos);

        return categoryDtos;
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %s не найдена.", categoryId)));

        return categoryMapper.categoryToDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    private void throwExistName(String name) {
        if (Boolean.TRUE.equals(categoryRepository.existsByName(name))) {
            throw new ConflictException(String.format("Категория с id = %s уже существует.", name));
        }
    }
}

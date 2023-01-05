package ru.practicum.ewmmain.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

import static ru.practicum.ewmmain.category.mapper.CategoryMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        throwExistName(newCategoryDto.getName());
        Category category = dtoNewToCategory(newCategoryDto);
        category = categoryRepository.save(category);
        log.info("Создана категория: {}.", category);

        return categoryToDto(category);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        throwExistName(categoryDto.getName());
        Category category = dtoToCategory(categoryDto);
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.debug("Категория с id={} обновлена.", category.getId());

        return categoryToDto(category);
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        List<CategoryDto> categoryDtos = categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::categoryToDto)
                .collect(Collectors.toList());
        log.info("Получение категориий: {}.", categoryDtos);

        return categoryDtos;
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %s не найдена.", categoryId)));

        return categoryToDto(category);
    }

    @Override
    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    private void throwExistName(String name) {
        if (Boolean.TRUE.equals(categoryRepository.existsByName(name))) {
            throw new ConflictException(String.format("Категория с id = %s уже существует.", name));
        }
    }
}

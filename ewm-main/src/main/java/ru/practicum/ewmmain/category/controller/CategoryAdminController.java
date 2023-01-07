package ru.practicum.ewmmain.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.dto.NewCategoryDto;
import ru.practicum.ewmmain.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
@Validated
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Вызван метод createCategory() в CategoryAdminController.");
        CategoryDto createCategory = categoryService.createCategory(newCategoryDto);

        return ResponseEntity.ok().body(createCategory).getBody();
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Вызван метод updateCategory() в CategoryAdminController.");
        CategoryDto updateCategory = categoryService.updateCategory(categoryDto);

        return ResponseEntity.ok().body(updateCategory).getBody();
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable(name = "catId") @NotNull Long id) {
        log.info("Вызван метод deleteCategory() в CategoryAdminController для категории с id {}.", id);
        categoryService.deleteCategory(id);

        return ResponseEntity.ok().build();
    }
}
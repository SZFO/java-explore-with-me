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
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Вызван метод create() в CategoryAdminController.");
        CategoryDto create = categoryService.create(newCategoryDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @PatchMapping
    public CategoryDto update(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Вызван метод update() в CategoryAdminController.");
        CategoryDto update = categoryService.update(categoryDto);

        return ResponseEntity.ok().body(update).getBody();
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable(name = "catId") @NotNull Long id) {
        log.info("Вызван метод delete() в CategoryAdminController для категории с id {}.", id);
        categoryService.delete(id);

        return ResponseEntity.ok().build();
    }
}
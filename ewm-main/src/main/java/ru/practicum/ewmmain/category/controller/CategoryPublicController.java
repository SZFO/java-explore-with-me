package ru.practicum.ewmmain.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.service.CategoryService;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class CategoryPublicController {
    private final CategoryService categoryService;


    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@NotNull @PathVariable(name = "catId") Long id) {
        log.info("Вызван метод getCategoryById() в CategoryPublicController для категории с id {}.", id);
        CategoryDto getCategoryById = categoryService.getCategoryById(id);

        return ResponseEntity.ok().body(getCategoryById).getBody();
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Вызван метод getAllCategories() в CategoryPublicController, где " +
                "индекс первого элемента = {}, количество элементов для отображения {}", from, size);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<CategoryDto> getAllCategories = categoryService.getAllCategories(pageRequest);

        return ResponseEntity.ok().body(getAllCategories).getBody();
    }
}
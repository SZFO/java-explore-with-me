package ru.practicum.ewmmain.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Вызван метод createCompilation() в CompilationAdminController.");
        CompilationDto createCompilation = compilationService.createCompilation(newCompilationDto);

        return ResponseEntity.ok().body(createCompilation).getBody();
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<HttpStatus> deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("Вызван метод deleteCompilation() в CompilationAdminController для подборки событий с id {}.", compId);
        compilationService.deleteCompilation(compId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> deleteEventFromCompilation(@PathVariable @Positive Long compId,
                                                                 @PathVariable @Positive Long eventId) {
        log.info("Вызван метод deleteEventFromCompilation() в CompilationAdminController для события с  id {} в " +
                "подборке событий с id {}.", eventId, compId);
        compilationService.deleteEventFromCompilation(compId, eventId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> addEventInCompilation(@PathVariable @Positive Long compId,
                                                            @PathVariable @Positive Long eventId) {
        log.info("Вызван метод addEventInCompilation() в CompilationAdminController для события с  id {} в " +
                "подборку событий с id {}.", eventId, compId);
        compilationService.addEventInCompilation(compId, eventId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> unpinCompilation(@PathVariable @Positive Long compId) {
        log.info("Вызван метод unpinCompilation() в CompilationAdminController для подборки событий с id {}.", compId);
        compilationService.unpinCompilation(compId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> pinCompilation(@PathVariable @Positive Long compId) {
        log.info("Вызван метод pinCompilation() в CompilationAdminController для подборки событий с id {}.", compId);
        compilationService.pinCompilation(compId);

        return ResponseEntity.ok().build();
    }
}
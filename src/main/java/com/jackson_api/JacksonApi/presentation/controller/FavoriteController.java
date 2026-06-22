package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateFavoriteRequest;
import com.jackson_api.JacksonApi.application.dto.response.FavoriteResponse;
import com.jackson_api.JacksonApi.application.service.FavoriteService;
import com.jackson_api.JacksonApi.presentation.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<PagedResponse<FavoriteResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(PagedResponse.from(favoriteService.findAllFavorites(pageable)));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites() {
        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.getFavoritesForCurrentUser());
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FavoriteResponse>> findByUser(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.findFavoritesByUser(id));
    }

    @PostMapping()
    public ResponseEntity<FavoriteResponse> add(@Valid @RequestBody CreateFavoriteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.addFavorite(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package com.sisdistro.tweetapp.controller;

import com.sisdistro.tweetapp.dto.TweetRequest;
import com.sisdistro.tweetapp.dto.TweetResponse;
import com.sisdistro.tweetapp.service.TweetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST para gerenciamento de tweets.
 */
@RestController
@RequestMapping("/api/tweets")
@Tag(name = "Tweets", description = "Operações de CRUD para tweets")
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @Operation(summary = "Cria um novo tweet")
    @PostMapping
    public ResponseEntity<TweetResponse> create(@Valid @RequestBody TweetRequest request) {
        TweetResponse response = tweetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lista todos os tweets")
    @GetMapping
    public ResponseEntity<List<TweetResponse>> listAll() {
        return ResponseEntity.ok(tweetService.findAll());
    }

    @Operation(summary = "Lista tweets por autor")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<TweetResponse>> listByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(tweetService.findByAuthor(authorId));
    }

    @Operation(summary = "Busca um tweet pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<TweetResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tweetService.findById(id));
    }

    @Operation(summary = "Atualiza um tweet existente")
    @PutMapping("/{id}")
    public ResponseEntity<TweetResponse> update(@PathVariable Long id, @Valid @RequestBody TweetRequest request) {
        return ResponseEntity.ok(tweetService.update(id, request));
    }

    @Operation(summary = "Remove um tweet")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

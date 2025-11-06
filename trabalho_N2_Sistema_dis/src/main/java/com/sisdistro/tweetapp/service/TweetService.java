package com.sisdistro.tweetapp.service;

import com.sisdistro.tweetapp.domain.Tweet;
import com.sisdistro.tweetapp.domain.User;
import com.sisdistro.tweetapp.dto.TweetRequest;
import com.sisdistro.tweetapp.dto.TweetResponse;
import com.sisdistro.tweetapp.exception.BusinessException;
import com.sisdistro.tweetapp.exception.ResourceNotFoundException;
import com.sisdistro.tweetapp.repository.TweetRepository;
import com.sisdistro.tweetapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio relacionadas às operações de {@link Tweet}.
 */
@Service
@Transactional
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    public TweetResponse create(TweetRequest request) {
        User author = authenticateAuthor(request.authorHandle(), request.password());
        Tweet tweet = new Tweet();
        tweet.setAuthor(author);
        tweet.setContent(request.content());
        Tweet saved = tweetRepository.save(tweet);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TweetResponse> findAll() {
        return tweetRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TweetResponse> findByAuthor(Long authorId) {
        return tweetRepository.findByAuthorIdOrderByCreatedAtDesc(authorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TweetResponse findById(Long id) {
        return toResponse(getTweetOrThrow(id));
    }

    public TweetResponse update(Long id, TweetRequest request) {
        Tweet tweet = getTweetOrThrow(id);
        User author = authenticateAuthor(request.authorHandle(), request.password());
        if (!tweet.getAuthor().getId().equals(author.getId())) {
            throw new BusinessException("Apenas o autor original pode atualizar o tweet.");
        }
        tweet.setContent(request.content());
        Tweet updated = tweetRepository.saveAndFlush(tweet);
        return toResponse(updated);
    }

    public void delete(Long id) {
        Tweet tweet = getTweetOrThrow(id);
        tweetRepository.delete(tweet);
    }

    private Tweet getTweetOrThrow(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet não encontrado: " + id));
    }

    private User authenticateAuthor(String handle, String password) {
        return userRepository.findByHandleIgnoreCase(handle)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new BusinessException("Handle ou senha inválidos."));
    }

    private TweetResponse toResponse(Tweet tweet) {
        User author = tweet.getAuthor();
        return new TweetResponse(
                tweet.getId(),
                tweet.getContent(),
                tweet.getCreatedAt(),
                tweet.getUpdatedAt(),
                author.getId(),
                author.getHandle(),
                author.getDisplayName()
        );
    }
}

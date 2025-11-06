package com.sisdistro.tweetapp.service;

import com.sisdistro.tweetapp.domain.User;
import com.sisdistro.tweetapp.dto.UserRequest;
import com.sisdistro.tweetapp.dto.UserResponse;
import com.sisdistro.tweetapp.exception.BusinessException;
import com.sisdistro.tweetapp.exception.ResourceNotFoundException;
import com.sisdistro.tweetapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio relacionadas às operações de {@link User}.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(UserRequest request) {
        validateHandleAvailability(request.handle(), null);
        User user = new User();
        applyRequest(user, request);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = getUserOrThrow(id);
        return toResponse(user);
    }

    public UserResponse update(Long id, UserRequest request) {
        User user = getUserOrThrow(id);
        validateHandleAvailability(request.handle(), id);
        applyRequest(user, request);
        return toResponse(user);
    }

    public void delete(Long id) {
        User user = getUserOrThrow(id);
        userRepository.delete(user);
    }

    private void applyRequest(User user, UserRequest request) {
        user.setHandle(request.handle());
        user.setPassword(request.password());
        user.setDisplayName(request.displayName());
        user.setProfileImageUrl(request.profileImageUrl());
        user.setBio(request.bio());
    }

    private void validateHandleAvailability(String handle, Long currentUserId) {
        boolean exists = userRepository.findByHandleIgnoreCase(handle)
                .filter(found -> !found.getId().equals(currentUserId))
                .isPresent();
        if (exists) {
            throw new BusinessException("Já existe um usuário com o @handle informado.");
        }
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private UserResponse toResponse(User user) {
        long tweetCount = user.getTweets() != null ? user.getTweets().size() : 0;
        return new UserResponse(
                user.getId(),
                user.getHandle(),
                user.getDisplayName(),
                user.getProfileImageUrl(),
                user.getBio(),
                tweetCount
        );
    }
}

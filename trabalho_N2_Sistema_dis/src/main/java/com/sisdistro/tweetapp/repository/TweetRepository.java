package com.sisdistro.tweetapp.repository;

import com.sisdistro.tweetapp.domain.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório para operações de {@link Tweet}.
 */
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    List<Tweet> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    List<Tweet> findAllByOrderByCreatedAtDesc();
}

package com.workable.movieramatestapp.service;

import com.workable.movieramatestapp.domain.Vote;

import java.util.Optional;

public interface VoteService {

    Optional<Vote> findVote( Long userId,Long movieId);

    boolean deleteVote(Long id);

    Vote vote(Vote vote);
}

package com.workable.movieramatestapp.dao;

import com.workable.movieramatestapp.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteDao extends JpaRepository<Vote, Long> {


    Optional<Vote> findByUserIdAndMovieId(Long userId, Long movieId);


}

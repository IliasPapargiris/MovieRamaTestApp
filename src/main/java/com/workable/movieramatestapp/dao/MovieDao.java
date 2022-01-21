package com.workable.movieramatestapp.dao;

import com.workable.movieramatestapp.domain.Movie;
import com.workable.movieramatestapp.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieDao extends JpaRepository<Movie, Long> {

    List<Movie> findAll();

    List<Movie> findMoviesByUserId(Long userId);


    Boolean existsByIdAndUser(Long movieId, User user);

    @Override
    Page<Movie> findAll(Pageable pageable);


}

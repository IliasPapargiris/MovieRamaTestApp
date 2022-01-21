package com.workable.movieramatestapp.service;


import com.workable.movieramatestapp.domain.Movie;
import com.workable.movieramatestapp.domain.User;
import com.workable.movieramatestapp.dto.MovieDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MovieService {


    Optional<Movie> saveMovie(Long id, String title, String description, User user);


    List<Movie> getUsersMovies(Long userId);

    Boolean movieExists(Movie movie, User user);

    Boolean movieExists(MovieDto movie, User user);

    Optional<Movie> findMovieById(Long id);

    Page<Movie> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);


}

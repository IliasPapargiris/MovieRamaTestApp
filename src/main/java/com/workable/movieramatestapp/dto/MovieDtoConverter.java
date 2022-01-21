package com.workable.movieramatestapp.dto;

import com.workable.movieramatestapp.domain.Movie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieDtoConverter {

    public List<MovieDto> entityListConvertToDtoList(List<Movie> movies) {
        return movies.stream()
                .map(m -> entityMovieToDto(m))
                .collect(Collectors.toList());
    }

    private MovieDto entityMovieToDto(Movie movie) {
        long likesCount = movie.getVotes()
                .stream()
                .filter(v -> v.getIsLiked() == true)
                .count();

        long hatesCount = movie.getVotes()
                .stream()
                .filter(v -> v.getIsLiked() == false)
                .count();

        MovieDto m = new MovieDto();

        m.setUsername(movie.getUser().getUsername());
        m.setUserId(movie.getUser().getId());
        m.setTitle(movie.getTitle());
        m.setDescription(movie.getDescription());
        m.setPublicationDate(movie.getPublicationDate());
        m.setHates(hatesCount);
        m.setLikes(likesCount);
        m.setId(movie.getId());
        return m;
    }
}

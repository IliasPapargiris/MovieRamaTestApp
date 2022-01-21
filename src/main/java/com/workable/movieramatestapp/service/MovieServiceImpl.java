package com.workable.movieramatestapp.service;

import com.google.common.collect.Lists;
import com.workable.movieramatestapp.dao.MovieDao;
import com.workable.movieramatestapp.dao.UserDao;
import com.workable.movieramatestapp.domain.Movie;
import com.workable.movieramatestapp.domain.User;
import com.workable.movieramatestapp.domain.Vote;
import com.workable.movieramatestapp.dto.MovieDto;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieDao movieDao;
    private final UserDao userDao;

    public enum PROPERTIES {
        title,
        publicationDate,
        likes,
        hates
    }


    public MovieServiceImpl(MovieDao movieDao, UserDao userDao) {
        this.movieDao = movieDao;
        this.userDao = userDao;
    }


    @Override
    @Transactional
    public Optional<Movie> saveMovie(Long movieId, String title, String description, User user) {
        if (movieDao.existsByIdAndUser(movieId, user)) {
            Movie persistedMovie = movieDao.save(new Movie(title, description, userDao.findById(user.getId()).get(), new Date()));
            return Optional.of(persistedMovie);
        }
        Movie persistedMovie = movieDao.save(new Movie(title, description, userDao.findById(user.getId()).get(), new Date()));
        return Optional.ofNullable(null);
    }

    @Override
    public List<Movie> getUsersMovies(Long userId) {
        return movieDao.findMoviesByUserId(userId);
    }

    @Override
    public Boolean movieExists(Movie movie, User user) {
        return movieDao.existsByIdAndUser(movie.getId(), user);
    }

    @Override
    public Boolean movieExists(MovieDto movie, User user) {
        return movieDao.existsByIdAndUser(movie.getId(), user);
    }

    @Override
    public Optional<Movie> findMovieById(Long id) {
        return movieDao.findById(id);
    }

    @Override
    public Page<Movie> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {

        List<Movie> all = movieDao.findAll();

        List<Movie> toBeDisplayedMovies;
        if (sortField.equals("likes")) {
            List<Movie> likedMovies = getMoviesByMoreLikes();
            if (likedMovies.isEmpty()) {
                toBeDisplayedMovies = Collections.emptyList();
            }
            List<List<Movie>> moviePages = Lists.partition(likedMovies, 3);
            toBeDisplayedMovies = moviePages.get(pageNo - 1);
            Page<Movie> createdPage = new PageImpl<Movie>(toBeDisplayedMovies, PageRequest.of(pageNo, pageSize), likedMovies.size());
            return createdPage;
        } else if (sortField.equals("hates")) {
            List<Movie> hatedMovies = getMoviesByMoreHates();
            if (hatedMovies.isEmpty()) {
                toBeDisplayedMovies = Collections.emptyList();
            }
            List<List<Movie>> moviePages = Lists.partition(hatedMovies, 3);
            toBeDisplayedMovies = moviePages.get(pageNo - 1);
            Page<Movie> createdPage = new PageImpl<Movie>(toBeDisplayedMovies, PageRequest.of(pageNo, pageSize), hatedMovies.size());
            return createdPage;
        }

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();


        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return movieDao.findAll(pageable);
    }

    private List<Movie> getMoviesByMoreLikes() {
        List<Movie> all = movieDao.findAll();

        HashMap<Movie, Long> idbyLikes = new HashMap<>();
        Predicate<Vote> isMovieLiked = v -> v.getIsLiked() == true;


        for (Movie m : all) {

            Long likes = m.getVotes()
                    .stream()
                    .filter(isMovieLiked)
                    .count();


            idbyLikes.put(m, likes);

        }
        List<Movie> sortedLikedMovies = idbyLikes
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(m -> m.getKey()).collect(Collectors.toList());


        return sortedLikedMovies;
    }

    private List<Movie> getMoviesByMoreHates() {
        List<Movie> all = movieDao.findAll();

        HashMap<Movie, Long> idbyHates = new HashMap<>();
        Predicate<Vote> isMovieHated = v -> v.getIsLiked() == false;

        for (Movie m : all) {

            Long hates = m.getVotes()
                    .stream()
                    .filter(isMovieHated)
                    .count();

            idbyHates.put(m, hates);

        }

        List<Movie> sortedHatedMovies = idbyHates
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(m -> m.getKey()).collect(Collectors.toList());

        return sortedHatedMovies;
    }

}

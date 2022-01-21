package com.workable.movieramatestapp.controller;

import com.workable.movieramatestapp.domain.Movie;
import com.workable.movieramatestapp.domain.User;
import com.workable.movieramatestapp.domain.Vote;
import com.workable.movieramatestapp.dto.MovieDto;
import com.workable.movieramatestapp.dto.MovieDtoConverter;
import com.workable.movieramatestapp.service.MovieService;
import com.workable.movieramatestapp.service.MyUserDetails;
import com.workable.movieramatestapp.service.UserService;
import com.workable.movieramatestapp.service.VoteService;
import com.workable.movieramatestapp.util.SortfieldProperties;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/")
public class MovieController {

    private static final Integer PAGE_SIZE = 3;

    private static final String USER_ROLE = "user";
    private static final String TITLE = "title";
    private static final String ASC = "asc";
    private final MovieService movieService;
    private final UserService userService;
    private final VoteService voteService;

    private final MovieDtoConverter movieDtoConverter;

    public MovieController(MovieService movieService, UserService userService, VoteService voteService, MovieDtoConverter movieDtoConverter) {
        this.movieService = movieService;
        this.userService = userService;
        this.voteService = voteService;
        this.movieDtoConverter = movieDtoConverter;
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") Optional<Integer> pageNo, Model model,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir) {


        Page<Movie> moviePage;

        boolean userIsLoged = SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
        boolean userNotLoged = !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails);

        if (Arrays.stream(SortfieldProperties.values()).anyMatch(v -> v.name().equals(sortField))) {

            moviePage = movieService.findPaginated(pageNo.orElse(1), PAGE_SIZE, sortField, ASC);

            List<Movie> listMovies = moviePage.getContent();

            List<MovieDto> dtos = movieDtoConverter.entityListConvertToDtoList(listMovies);

            for (MovieDto movie : dtos) {
                if (userIsLoged) {
                    MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    if (movieService.movieExists(movie, userDetails.user)) {
                        movie.setValidForVoting(false);
                    } else {
                        if (voteService.findVote(userDetails.user.getId(), movie.getId()).isPresent()) {
                            movie.setHasNotBeenVotedByUser(true);
                        }
                        movie.setValidForVoting(true);
                    }
                }
            }


            Integer totalPages = moviePage.getTotalPages();
            totalPages = (PAGE_SIZE > dtos.size()) ? totalPages - 1 : moviePage.getTotalPages();

            String asc = Optional.of(sortField).orElse("asc");
            String likes = "likes";
            String hates = "hates";
            model.addAttribute("currentPage", pageNo.orElse(1));
            model.addAttribute("logedin", userIsLoged);
            model.addAttribute("notloged", userNotLoged);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("listMovies", listMovies);
            model.addAttribute("dtos", dtos);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", asc);
            model.addAttribute("likes", likes);
            model.addAttribute("hates", hates);
            return "index";
        }
        model.addAttribute("logedin", userIsLoged);
        return viewHomePage(model);

    }

    @GetMapping
    public String viewHomePage(Model model) {
        return findPaginated(Optional.of(Integer.valueOf(1)), model, TITLE,ASC );
    }

    @PreAuthorize("hasRole(USER_ROLE)")
    @PostMapping("/add")
    public String addedMovie(@ModelAttribute @Valid Movie movie, BindingResult bindingResult, Model model) {

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (bindingResult.hasErrors()) {
            return "addmovies";
        }
        if (movieService.movieExists(movie, userDetails.user)) {
            model.addAttribute("error", "Movie failed to be added");
            return "addmovies";
        }
        Optional<Movie> persisted = movieService.saveMovie(movie.getId(), movie.getTitle(), movie.getDescription(), userDetails.user);

        if (!persisted.isPresent()) {
            model.addAttribute("error", "Movie failed to be added,did you already added movie:" + movie.getTitle());
            return "addmovies";
        }

        return viewHomePage(model);
    }

    @GetMapping("/add")
    public String addedMovie(Model model) {
        Movie movie = new Movie();
        model.addAttribute("movie", movie);
        return "addmovies";
    }


    @RequestMapping("/moviesByUser/{userId}")
    public String moviesByUser(@PathVariable(value = "userId") Optional<Long> userId, Model model) {

        if (userId.isPresent()) {
            Optional<User> user = userService.findUser(userId.get());
            if (user.isPresent()) {
                List<Movie> usersMovies = movieService.getUsersMovies(user.get().getId());
                List<MovieDto> movieDtos = movieDtoConverter.entityListConvertToDtoList(usersMovies);
                model.addAttribute("movies", movieDtos);
                model.addAttribute("name", user.get().getFirstName());
                model.addAttribute("lastName", user.get().getLastName());
                model.addAttribute("username", user.get().getUsername());
                return "moviesbyuser";
            }
        }
        return viewHomePage(model);
    }

    @PreAuthorize("hasRole(USER_ROLE)")
    @RequestMapping("/voteMovie/{movieId}")
    public String voteMovie(@PathVariable Optional<Long> movieId, @RequestParam Boolean isLiked, Model model) {
        if (movieId.isPresent()) {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof MyUserDetails) {
                MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Long userId = userDetails.user.getId();
                Optional<Vote> optionalVote = voteService.findVote(userId, movieId.get());
                User user = userService.findUser(userId).get();
                Movie movie = movieService.findMovieById(movieId.get()).get();
                if (optionalVote.isPresent()) {
                    Vote vote = optionalVote.get();
                    vote.setIsLiked(isLiked);
                    voteService.vote(vote);
                } else {
                    voteService.vote(new Vote(user, movie, true));
                }
                return viewHomePage(model);
            }
        }

        return viewHomePage(model);
    }

    @PreAuthorize("hasRole(USER_ROLE)")
    @RequestMapping(value = "/deleteVote/{movieId}")
    public String deleteVote(@PathVariable Optional<Long> movieId, Model model) {

        if ((SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof MyUserDetails)
                && movieId.isPresent()) {
            MyUserDetails details = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<Vote> vote = voteService.findVote(details.user.getId(), movieId.get());
            if (vote.isPresent()) {
                voteService.deleteVote(vote.get().getId());
            }
        }
        return viewHomePage(model);

    }
}


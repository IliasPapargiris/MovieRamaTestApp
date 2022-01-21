package com.workable.movieramatestapp.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MovieDto {

    private Long id;

    private String title;

    private String description;

    private Date publicationDate;

    private String username;

    private Long userId;

    private Long likes;

    private Long hates;

    private Boolean validForVoting;

    private Boolean hasNotBeenVotedByUser;
}

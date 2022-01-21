package com.workable.movieramatestapp.service;

import com.workable.movieramatestapp.dao.VoteDao;
import com.workable.movieramatestapp.domain.Vote;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteDao voteDao;

    public VoteServiceImpl(VoteDao voteDao) {
        this.voteDao = voteDao;
    }

    @Override
    public Optional<Vote> findVote(Long userId, Long movieId) {
        return voteDao.findByUserIdAndMovieId(userId, movieId);
    }


    @Override
    @Transactional
    public boolean deleteVote(Long id) {
        voteDao.deleteById(id);

        return !voteDao.findById(id).isPresent();
    }

    @Override
    @Transactional
    public Vote vote(Vote vote) {
        return voteDao.save(vote);
    }
}

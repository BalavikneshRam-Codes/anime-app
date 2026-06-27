package com.bu.anime_web.service;

import com.bu.anime_web.entity.Episode;
import com.bu.anime_web.entity.User;
import com.bu.anime_web.entity.UserEpisodeProgress;
import com.bu.anime_web.repository.EpisodeRepository;
import com.bu.anime_web.repository.IUserRepository;
import com.bu.anime_web.repository.UserEpisodeProgressRepository;
import com.bu.anime_web.vo.Request.MarkEpisodeProgressRequestVO;
import com.bu.anime_web.vo.Response.MarkEpisodeProgressResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProgressService {

    @Autowired
    private UserEpisodeProgressRepository userEpisodeProgressRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    public MarkEpisodeProgressResponseVO markCompleted(MarkEpisodeProgressRequestVO request) {
        MarkEpisodeProgressResponseVO response = new MarkEpisodeProgressResponseVO();

        try {
            if (request.getUserId() == null || request.getEpisodeId() == null) {
                throw new IllegalArgumentException("User ID and Episode ID are required");
            }

            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }

            Optional<Episode> episodeOpt = episodeRepository.findById(request.getEpisodeId());
            if (episodeOpt.isEmpty()) {
                throw new IllegalArgumentException("Episode not found");
            }

            Optional<UserEpisodeProgress> existingProgress = userEpisodeProgressRepository
                    .findByUserIdAndEpisodeId(request.getUserId(), request.getEpisodeId());

            UserEpisodeProgress progress;
            if (existingProgress.isPresent()) {
                progress = existingProgress.get();
                // Update timestamp will be handled by @PreUpdate in entity
            } else {
                progress = new UserEpisodeProgress();
                progress.setUser(userOpt.get());
                progress.setEpisode(episodeOpt.get());
            }

            userEpisodeProgressRepository.save(progress);

            response.setStatus("success");
            response.setMessage("Episode marked as completed successfully");

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }

        return response;
    }
}

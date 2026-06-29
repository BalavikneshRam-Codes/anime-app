package com.bu.anime_web.converter;

import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.entity.Genre;
import com.bu.anime_web.entity.Producer;
import com.bu.anime_web.entity.Studio;
import com.bu.anime_web.repository.GenreRepository;
import com.bu.anime_web.repository.ProducerRepository;
import com.bu.anime_web.repository.StudioRepository;
import com.bu.anime_web.vo.common.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnimeConverter {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ProducerRepository producerRepository;
    @Autowired
    private StudioRepository studioRepository;

    public AnimeVO mapToAnimeVO(Anime anime) {
        AnimeVO vo = new AnimeVO();
        BeanUtils.copyProperties(anime, vo);

        vo.setEpisodes(anime.getEpisodesNum());

        TermsByType terms = new TermsByType();
        if (anime.getTypes() != null && !anime.getTypes().isEmpty()) {
            terms.setType(Arrays.asList(anime.getTypes().split(",")));
        }
        vo.setTermsByType(terms);

        if (anime.getGenresList() != null && !anime.getGenresList().isEmpty()) {
            List<GenreVO> genreVOs = anime.getGenresList().stream().map(g -> {
                GenreVO gvo = new GenreVO();
                gvo.setId(g.getId());
                gvo.setName(g.getName());
                return gvo;
            }).collect(Collectors.toList());
            vo.setGenresList(genreVOs);
        }

        if (anime.getProducerList() != null && !anime.getProducerList().isEmpty()) {
            List<ProducerVO> producerVOs = anime.getProducerList().stream().map(p -> {
                ProducerVO pvo = new ProducerVO();
                pvo.setId(p.getId());
                pvo.setName(p.getName());
                return pvo;
            }).collect(Collectors.toList());
            vo.setProducerList(producerVOs);
        }

        if (anime.getStudioList() != null && !anime.getStudioList().isEmpty()) {
            List<StudioVO> studioVOs = anime.getStudioList().stream().map(s -> {
                StudioVO svo = new StudioVO();
                svo.setId(s.getId());
                svo.setName(s.getName());
                return svo;
            }).collect(Collectors.toList());
            vo.setStudioList(studioVOs);
        }

        return vo;
    }

    public void mapToEntity(AnimeVO dto, Anime anime) {
        anime.setAnimeId(dto.getId());
        anime.setTitle(dto.getTitle());
        anime.setAlternative(dto.getAlternative());
        anime.setTitles(dto.getTitles());
        anime.setNativeTitle(dto.getNativeTitle());
        anime.setSlug(dto.getSlug());
        anime.setRating(dto.getRating());
        anime.setPoster(dto.getPoster());
        anime.setIsSub(dto.getIsSub());
        anime.setDescription(dto.getDescription());
        anime.setAired(dto.getAired());
        anime.setSeason(dto.getSeason());
        anime.setYear(dto.getYear());
        anime.setDuration(dto.getDuration());
        anime.setStatus(dto.getStatus());
        anime.setScore(dto.getScore());
        anime.setMalId(dto.getMalId());
        anime.setEpisodesNum(dto.getEpisodes());
        anime.setAniId(dto.getAniId());
        anime.setSource(dto.getSource());
        anime.setBackgroundImage(dto.getBackgroundImage());
        anime.setUpdatedAt(dto.getUpdatedAt());
        anime.setNextAirScheduleTime(dto.getNextAirScheduleTime());
        anime.setNextAirEp(dto.getNextAirEp());

        if (dto.getTermsByType() != null) {
            if (dto.getTermsByType().getGenre() != null) {
                List<Genre> genres = new ArrayList<>();
                for (String g : dto.getTermsByType().getGenre()) {
                    String genreName = g.trim();
                    if (!genreName.isEmpty()) {
                        Genre genre = genreRepository.findByName(genreName).orElseGet(() -> {
                            Genre newGenre = new Genre();
                            newGenre.setName(genreName);
                            return genreRepository.save(newGenre);
                        });
                        if (!genres.contains(genre)) {
                            genres.add(genre);
                        }
                    }
                }
                anime.setGenresList(genres);
            }
            if (dto.getTermsByType().getProducers() != null) {
                List<Producer> producers = new ArrayList<>();
                for (String p : dto.getTermsByType().getProducers()) {
                    String producerName = p.trim();
                    if (!producerName.isEmpty()) {
                        Producer producer = producerRepository.findByName(producerName).orElseGet(() -> {
                            Producer newProducer = new Producer();
                            newProducer.setName(producerName);
                            return producerRepository.save(newProducer);
                        });
                        if (!producers.contains(producer)) {
                            producers.add(producer);
                        }
                    }
                }
                anime.setProducerList(producers);
            }
            if (dto.getTermsByType().getStudios() != null) {
                List<Studio> studios = new ArrayList<>();
                for (String s : dto.getTermsByType().getStudios()) {
                    String studioName = s.trim();
                    if (!studioName.isEmpty()) {
                        Studio studio = studioRepository.findByName(studioName).orElseGet(() -> {
                            Studio newStudio = new Studio();
                            newStudio.setName(studioName);
                            return studioRepository.save(newStudio);
                        });
                        if (!studios.contains(studio)) {
                            studios.add(studio);
                        }
                    }
                }
                anime.setStudioList(studios);
            }
            if (dto.getTermsByType().getType() != null) {
                anime.setTypes(String.join(",", dto.getTermsByType().getType()));
            }
        }
    }
}

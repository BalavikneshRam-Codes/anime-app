package com.bu.anime_web.repository;

import com.bu.anime_web.entity.Anime;
import com.bu.anime_web.vo.Request.LoadAnimeRequestVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class AnimeCustomRepositoryImpl implements AnimeCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Anime> findAnimesWithDynamicFilters(LoadAnimeRequestVO request, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Anime> query = cb.createQuery(Anime.class);
        Root<Anime> anime = query.from(Anime.class);
        query.distinct(true);

        List<Predicate> predicates = buildPredicates(request, cb, anime);

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Apply a default sort or dynamic sort
        if ("score".equalsIgnoreCase(request.getSortBy())) {
            Expression<Double> scoreDouble = cb.nullif(anime.get("score"), "").as(Double.class);
            query.orderBy(cb.desc(scoreDouble));
        } else {
            query.orderBy(cb.desc(anime.get("id")));
        }

        TypedQuery<Anime> typedQuery = entityManager.createQuery(query);

        // Apply pagination offsets
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Anime> content = typedQuery.getResultList();

        // Create count query for pagination total
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Anime> countRoot = countQuery.from(Anime.class);
        countQuery.select(cb.countDistinct(countRoot));
        
        List<Predicate> countPredicates = buildPredicates(request, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }
        
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(LoadAnimeRequestVO request, CriteriaBuilder cb, Root<Anime> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            String titlePattern = "%" + request.getTitle().trim().toLowerCase() + "%";
            Predicate titleMatch = cb.like(cb.lower(root.get("title")), titlePattern);
            Predicate titlesMatch = cb.like(cb.lower(root.get("titles")), titlePattern);
            predicates.add(cb.or(titleMatch, titlesMatch));
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            String typePattern = "%" + request.getType().trim().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("types")), typePattern));
        }

        if (request.getGenres() != null && !request.getGenres().trim().isEmpty()) {
            String genrePattern = "%" + request.getGenres().trim().toLowerCase() + "%";
            jakarta.persistence.criteria.Join<Anime, com.bu.anime_web.entity.Genre> genresJoin = root.join("genresList", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(genresJoin.get("name")), genrePattern));
        }

        if (request.getSeason() != null && !request.getSeason().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("season")), request.getSeason().trim().toLowerCase()));
        }

        if (request.getRating() != null && !request.getRating().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("rating")), request.getRating().trim().toLowerCase()));
        }

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("status")), request.getStatus().trim().toLowerCase()));
        }

        return predicates;
    }
}

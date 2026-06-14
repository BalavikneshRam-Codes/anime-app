package com.bu.anime_web.repository;

import com.bu.anime_web.entity.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {
    Optional<Producer> findByName(String name);
}

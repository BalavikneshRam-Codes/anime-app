package com.bu.anime_web.entity;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
public class Studio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We make this unique so we don't have "Action" saved 50 times
    @Column(unique = true, nullable = false)
    private String name;
}

package com.bu.anime_web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class EmbedUrl {

    // Using length = 500 because URLs can sometimes exceed 255 characters
    @Column(name = "embed_sub_url", length = 500)
    private String sub;

    @Column(name = "embed_dub_url", length = 500)
    private String dub;
}

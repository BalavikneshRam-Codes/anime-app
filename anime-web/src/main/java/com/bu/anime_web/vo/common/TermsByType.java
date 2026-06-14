package com.bu.anime_web.vo.common;

import lombok.Data;

import java.util.List;

@Data
public class TermsByType {
    private List<String> genre;
    private List<String> producers;
    private List<String> studios;
    private List<String> type;
}

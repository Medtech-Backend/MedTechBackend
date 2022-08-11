package com.project.medtech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContentDto {

    private Long contentId;

    private String imageUrl;

    private String header;

    private String subtitle;

    private String description;

    private Integer weekNumber;

    private Integer order;

}

package com.project.medtech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`content`")
public class ContentEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "content_seq"
    )
    @SequenceGenerator(
            name = "content_seq",
            sequenceName = "content_seq",
            allocationSize = 1
    )
    private Long id;

    private Integer weekNumber;

    @Column(name = "`order`")
    private Integer order;

    private String header;

    private String subtitle;

    private String description;

    private String imageUrl;

}

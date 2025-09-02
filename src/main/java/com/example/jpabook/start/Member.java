package com.example.jpabook.start;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MEMBER")
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "NAME")
    private String username;
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

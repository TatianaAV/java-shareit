package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, length = 25, unique = true)
    private String email;
}
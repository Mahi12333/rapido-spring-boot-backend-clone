package com.maven.Rapido.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maven.Rapido.emun.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "rolename")
    private UserRole roleName;


    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();


    public Role(UserRole roleName) {
        this.roleName = roleName;
    }
}

package com.workable.movieramatestapp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "First name is mandatory")
    @Size(max = 100)
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 100)
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Length(max = 100)
    private String password;

    @Column(nullable = false)
    @Length(min = 4, max = 4)
    private String role;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is mandatory")
    @Length(max = 100)
    private String username;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Movie> movies;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Vote> votes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastname='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + username + '\'' +
                '}';
    }
}
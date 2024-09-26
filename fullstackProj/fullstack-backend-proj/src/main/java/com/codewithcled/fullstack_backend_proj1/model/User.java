package com.codewithcled.fullstack_backend_proj1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity

public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String role = "user";
    private Integer elo;

//    public List<Tournament> getTournamentsParticipated() {
//        return tournamentsParticipated;
//    }
//
//
//    public void setTournamentsParticipated(List<Tournament> tournamentsParticipated) {
//        this.tournamentsParticipated = tournamentsParticipated;
//    }

//    @ManyToMany
//    @JoinTable(
//            name = "user_tournament", // Join table name
//            joinColumns = @JoinColumn(name = "user_id"), // Column for user
//            inverseJoinColumns = @JoinColumn(name = "tournament_id") // Column for tournament
//    )
//    private List<Tournament> tournamentsParticipated = new ArrayList<>();

    @JsonIgnore
    @ElementCollection
    private List<Long> tournamentsParticipating = new ArrayList();

    public List<Long> getTournamentsParticipating() {
        return tournamentsParticipating;
    }

    public void addParticipatingTournament(Long tournament_id){
        tournamentsParticipating.add(tournament_id);
    }

    public void removeParticipatingTournament(Tournament tournament){
        for (Long current: tournamentsParticipating){
            if (Objects.equals(current, tournament.getId())){
                tournamentsParticipating.remove(current);
                break;
            }
        }
    }

    public void setTournamentsParticipating(List<Long> tournamentsParticipated) {
        this.tournamentsParticipating = tournamentsParticipated;
    }

    public Integer getElo() {
        return elo;
    }

    public void setElo(Integer elo) {
        this.elo = elo;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}

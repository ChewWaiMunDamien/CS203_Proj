package com.codewithcled.fullstack_backend_proj1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.*;


@Entity
public class Tournament {

    @Id
    @GeneratedValue
    private Long id;
    private String tournament_name;
   @ElementCollection
    private List<Long> participants = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "tournament_scoreboard", joinColumns = @JoinColumn(name = "tournament_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "score")
    private Map<Long, Integer> scoreboard;
    private String date;
    private String status;

    private Integer size ;
    private Integer currentSize = 0;

    private Integer noOfRounds = 0;

    public Integer getNoOfRounds() {
        return noOfRounds;
    }

    public void setNoOfRounds(Integer noOfRounds) {
        this.noOfRounds = noOfRounds;
    }

    public Integer getCurrentSize(){
        return currentSize;
    }

    public void setCurrentSize(Integer currentSize){
        this.currentSize = currentSize;
    }
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setActive(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addParticipant(Long user_id){
        participants.add(user_id);
    }

    public void removeParticipant(User user){
        for (Long current: participants){

            if (Objects.equals(current, user.getId())){
                participants.remove(current);
                System.out.println(user.getId() + " deleted!");
                break;
            }
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTournament_name() {
        return tournament_name;
    }

    public void setTournament_name(String tournament_name) {
        this.tournament_name = tournament_name;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }


    public Map<Long, Integer> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Map<Long, Integer> scoreboard) {
        this.scoreboard = scoreboard;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

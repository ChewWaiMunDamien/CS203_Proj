package com.codewithcled.fullstack_backend_proj1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Match {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    private Long player1;
    private Double player1StartingElo;
    private Long player2;
    private Double player2StartingElo;

    private boolean isComplete = false;

    //-1 player 1 win, 0- draw, 1  player 2 win
    private Integer result;
    private Double eloChange1;
    private Double eloChange2;
    
    public void setPlayer1StartingElo(Double elo) {
        this.player1StartingElo = elo;
    }

    public Double getPlayer1StartingElo() {
        return this.player1StartingElo;
    }

    public void setPlayer2StartingElo(Double elo) {
        this.player2StartingElo = elo;
    }

    public Double getPlayer2StartingElo() {
        return this.player2StartingElo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public Long getPlayer1() {
        return player1;
    }

    public void setPlayer1(Long player1) {
        this.player1 = player1;
    }

    public Long getPlayer2() {
        return player2;
    }

    public void setPlayer2(Long player2) {
        this.player2 = player2;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Double getEloChange1() {
        return eloChange1;
    }

    public void setEloChange1(Double eloChange1) {
        this.eloChange1 = eloChange1;
    }

    public Double getEloChange2() {
        return eloChange2;
    }

    public void setEloChange2(Double eloChange2) {
        this.eloChange2 = eloChange2;
    }

    public void setIsComplete(boolean bool){
        this.isComplete = bool;
    }

    public boolean getIsComplete(){
        return this.isComplete;
    }
}

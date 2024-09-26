package com.codewithcled.fullstack_backend_proj1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.*;


@Entity
public class Match {
    @Id
    @GeneratedValue
    private Long id;

    private long player1Id;
    private long player2Id;

    private int result;

    private double eloChange1;
    private double eloChange2;


    public long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(long player1Id) {
        this.player1Id = player1Id;
    }

    public long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(long player2Id) {
        this.player2Id = player2Id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public double getEloChange1() {
        return eloChange1;
    }

    public void setEloChange1(double eloChange1) {
        this.eloChange1 = eloChange1;
    }

    public double getEloChange2() {
        return eloChange2;
    }

    public void setEloChange2(double eloChange2) {
        this.eloChange2 = eloChange2;
    }
}

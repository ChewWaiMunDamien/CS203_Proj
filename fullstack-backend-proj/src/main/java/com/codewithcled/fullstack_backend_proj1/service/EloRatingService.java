package com.codewithcled.fullstack_backend_proj1.service;

public interface EloRatingService {

    public double WinProbabilityOnElo(int elo1,int elo2);
    
    public double EloCalculation(int elo1,int elo2,int outcome,int k);

    public double WinValue(int outcome);
}

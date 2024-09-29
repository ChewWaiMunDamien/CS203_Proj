package com.codewithcled.fullstack_backend_proj1.service;


import org.springframework.stereotype.Service;

@Service
public class EloRatingServiceImplementation implements EloRatingService {

    @Override
    public double WinProbabilityOnElo(int eloA, int eloB) {
        // Takes in two Elo ratings, calculates the probability of Elo B winning over
        // EloA
        return 1.0 / (1 + Math.pow(10, (eloA - eloB) / 400.0));
    }

    @Override
    public double WinValue(int outcome) {
        // Integer Outcome, 1 for EloA win, 0 for EloB win, 0.5 if draw

        if (outcome == -1) {
            return 1;// A wins
        } else if (outcome == 1) {
            return 0;// B wins
        } else if (outcome == 0) {
            return 0.5;// Draw
        }

        return -1;
    }

    // Returns new ELo score of user A
    @Override
    public double EloCalculation(int eloA, int eloB, int outcome, int k) {

        if (k < 0 || eloA <= 0 || eloB <= 0) {
            throw new IllegalArgumentException("Invalid elo or k values"); // Should throw error invalid values
        }

        double winValue = WinValue(outcome);

        if (winValue == -1) {
            throw new IllegalArgumentException("Invalid match result only accepts -1 for A wins, 1 for B wins and 0 for draw");
        }

        // Probability of player A winning over player B
        double p1Win = WinProbabilityOnElo(eloA, eloB);

        return Math.round(eloA + k * (winValue - p1Win));
    }
}

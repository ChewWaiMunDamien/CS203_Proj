package com.codewithcled.fullstack_backend_proj1;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codewithcled.fullstack_backend_proj1.service.EloRatingServiceImplementation;

@ExtendWith(MockitoExtension.class)
public class EloRatingServiceTest {
    @InjectMocks
    private EloRatingServiceImplementation EloRatingService;

    @Test
    void isValidElo_ValidElo_returnTrue() {
        int elo = 1000;

        boolean result = EloRatingService.isValidElo(elo);

        assertEquals(true, result);
    }

    @Test
    void isValidElo_InValidEloNeg_returnFalse() {
        int elo = -1;

        boolean result = EloRatingService.isValidElo(elo);

        assertEquals(false, result);
    }

    @Test
    void getKValue_kGreaterThans1_returnsk2() {
        int elo = 2400 + 1;

        int result = EloRatingService.getKValue(elo);

        assertEquals(10, result);
    }

    @Test
    void getKValue_kEquals1_returnsk2() {
        int elo = 2400;

        int result = EloRatingService.getKValue(elo);

        assertEquals(10, result);
    }

    @Test
    void getKValue_kLesserThans1_returnsk1() {
        int elo = 2400 - 1;

        int result = EloRatingService.getKValue(elo);

        assertEquals(20, result);
    }

    @Test
    void WinProbabilityOnElo_validInputs_returnDouble() {
        int elo1 = 1000;
        int elo2 = 1000;

        double result = EloRatingService.WinProbabilityOnElo(elo1, elo2);

        assertEquals(0.5, result);
    }

    @Test
    void WinValue_outcome1_return1() {
        int outcome = -1;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(1, result);
    }

    @Test
    void WinValue_outcome2_return0() {
        int outcome = 1;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(0, result);
    }

    @Test
    void WinValue_outcome3_returnhalf() {
        int outcome = 0;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(0.5, result);
    }

    @Test
    void WinValue_noOutcome_returnneg1() {
        int outcome = 123;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(-1, result);
    }

    @Test
    void EloCalculation_InvalidElo_ReturnIllegalArgumentException() {
        int elo1 = -1;
        int elo2 = 1000;
        int outcome = 1;

        try {

            EloRatingService.EloCalculation(elo1, elo2, outcome);

        } catch (IllegalArgumentException e) {

            assertEquals("Invalid elo values", e.getMessage());

        }
    }

    @Test
    void EloCalculation_InvalidOutcome_ReturnIllegalArgumentException() {
        int elo1 = 1000;
        int elo2 = 1000;
        int outcome = 12;

        try {

            EloRatingService.EloCalculation(elo1, elo2, outcome);

        } catch (IllegalArgumentException e) {

            assertEquals("Invalid match result only accepts -1 for A wins, 1 for B wins and 0 for draw",
                    e.getMessage());

        }
    }

    @Test
    void EloChange_ReturnDouble() {
        int k = 20;
        double winProbability = 0.5;
        double winValue = 1;

        double result = EloRatingService.eloChange(k, winValue, winProbability);

        assertEquals(10, result);
    }

    @Test
    void EloChange_WV0_ReturnDouble() {
        int k = 20;
        double winProbability = 0.5;
        double winValue = 0;

        double result = EloRatingService.eloChange(k, winValue, winProbability);

        assertEquals(-10, result);
    }

    @Test
    void EloChange_WV05_ReturnDouble() {
        int k = 20;
        double winProbability = 0.5;
        double winValue = 0.5;

        double result = EloRatingService.eloChange(k, winValue, winProbability);

        assertEquals(0, result);
    }

    @Test
    void EloCalculation_ValidInputs_ReturnDouble() {
        int elo1 = 1000;
        int elo2 = 1000;
        int outcome = 1;

        try {
            double result = EloRatingService.EloCalculation(elo1, elo2, outcome);
            assertEquals(990,result);
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    void EloCalculation_ValidInputDiffK_ReturnDouble() {
        int elo1 = 2400;
        int elo2 = 2400;
        int outcome = 1;

        try {

            double result = EloRatingService.EloCalculation(elo1, elo2, outcome);
            assertEquals(2395, result);

        } catch (IllegalArgumentException e) {

        }
    }
}
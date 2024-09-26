package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;

import java.util.List;

public interface TournamentService {

    public List<Tournament> getAllTournament()  ;


    public Tournament findTournamentByName(String name) ;


}

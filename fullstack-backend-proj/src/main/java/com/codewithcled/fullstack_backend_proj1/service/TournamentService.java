package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;

import java.util.List;
import java.util.Optional;

public interface TournamentService {

    public List<Tournament> getAllTournament()  ;


    public Tournament findTournamentByName(String name) ;

    public List<Long> getTournamentParticipants (Long id) throws Exception;

    public Tournament updateUserParticipating (Long userId, Long id) throws Exception;

    public Tournament removeUserParticipating(Long userId, Long id) throws Exception;

    public Tournament updateTournament(Long id, Tournament newTournament) throws Exception;

}

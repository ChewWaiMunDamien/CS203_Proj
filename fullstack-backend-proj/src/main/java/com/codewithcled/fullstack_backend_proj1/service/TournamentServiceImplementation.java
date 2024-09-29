package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImplementation implements TournamentService{


    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    public TournamentServiceImplementation(TournamentRepository tournamentRepository) {
        this.tournamentRepository=tournamentRepository;
    }
    @Override
    public List<Tournament> getAllTournament() {
        return tournamentRepository.findAll();
    }

    @Override
    public Tournament findTournamentByName(String name) {
        return null;
    }

    @Override
    public List<Long> getTournamentParticipants(Long id) throws Exception {
        Tournament currentTournament =  tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Error Occured"));

        return currentTournament.getParticipants();
    }

    @Override
    public Tournament updateUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        currentTournament.addParticipant(userId);
        currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        return tournamentRepository.save(currentTournament);
    }

    @Override
    public Tournament removeUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Check if the user is in the participant list before attempting to remove
        if (currentTournament.getParticipants().contains(userId)) {
            currentTournament.removeParticipant(user);  // Remove the user
            currentTournament.setCurrentSize(currentTournament.getCurrentSize() - 1);
        } else {
            throw new Exception("User is not participating in the tournament");
        }

        return tournamentRepository.save(currentTournament);  // Save and return the updated tournament
    }
    @Override
    public Tournament updateTournament(Long id, Tournament newTournament) throws Exception {
        return tournamentRepository.findById(id)
                .map(tournament -> {
                    tournament.setTournament_name(newTournament.getTournament_name());
                    tournament.setDate(newTournament.getDate());
                    tournament.setSize(newTournament.getSize());
                    tournament.setActive(newTournament.getStatus());
                    tournament.setNoOfRounds(newTournament.getNoOfRounds());
                    tournament.setParticipants(newTournament.getParticipants());
                    return tournamentRepository.save(tournament);  // Save and return the updated tournament
                })
                .orElseThrow(() -> new Exception("Tournament not found"));  // Throw exception if tournament does not exist
    }



}

package com.codewithcled.fullstack_backend_proj1.controller;


import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class TournamentController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TournamentRepository tournamentRepository;

    @PostMapping("/tournament")
    Tournament newTournament (@RequestBody Tournament newTournament){
        return tournamentRepository.save(newTournament);
    }

    @GetMapping("/tournaments")
    List<Tournament> getAllTournaments(){
        return tournamentRepository.findAll();
    }

    @GetMapping("/tournament/{id}")
    Tournament getTournamentById(@PathVariable("id") Long id) throws Exception {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Error Occured"));
    }

    @GetMapping("/tournament/{id}/participant")
    List<Long> getTournamentParticipants(@PathVariable("id") Long id) throws Exception {
        Tournament currentTournament =  tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Error Occured"));

        return currentTournament.getParticipants();
    }

    @PutMapping("/tournament/{id}/participant/add")
    Tournament updateTournamentParticipant(@RequestParam("user_id") Long userId, @PathVariable("id") Long id) throws Exception {
        System.out.println(id + " and " + userId);
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        System.out.println(user);
        currentTournament.addParticipant(userId);
        System.out.println(currentTournament.getParticipants().size());
        currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        return tournamentRepository.save(currentTournament);
    }

    @PutMapping("/tournament/{id}/participant/delete")
    Tournament removePlayer(@RequestParam("user_id") Long userId, @PathVariable("id") Long id) throws Exception {
        System.out.println(id + " and delete " + userId);
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        currentTournament.removeParticipant(user);
        currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        return tournamentRepository.save(currentTournament);
    }

    @DeleteMapping("/tournament/{id}")
    String deleteTournament(@PathVariable("id") Long id){
//        if (!tournamentRepository.existsById(id)){
//
//        }
        tournamentRepository.deleteById(id);
        return "Tournament with " +id+ " has been deleted";
    }

    @PutMapping("/tournament/{id}")
    Tournament updateTournament(@RequestBody Tournament newTournament, @PathVariable("id") Long id) throws Exception {
        return tournamentRepository.findById(id)
                .map(tournament->{
                    tournament.setTournament_name(newTournament.getTournament_name());
                    tournament.setDate(newTournament.getDate());
                    tournament.setSize(newTournament.getSize());
                    tournament.setActive(newTournament.getStatus());
                    tournament.setNoOfRounds(newTournament.getNoOfRounds());
                    tournament.setParticipants(newTournament.getParticipants());
                    return tournamentRepository.save(tournament);
                }).orElseThrow(() -> new Exception("Error Occured"));
    }

}

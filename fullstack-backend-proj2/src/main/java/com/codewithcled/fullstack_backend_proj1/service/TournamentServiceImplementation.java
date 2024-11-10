package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
// import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TournamentServiceImplementation implements TournamentService {

    //for debugging purposes
    private static final Logger logger = Logger.getLogger(TournamentServiceImplementation.class.getName());

    @Autowired
    private TournamentRepository tournamentRepository;
    // @Autowired
    // private RoundRepository roundRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoundService roundService;

    @Override
    public List<Tournament> getAllTournament() {
        return tournamentRepository.findAll();
    }
    @Override
    public Tournament findTournamentByName(String name) {
        return null;
    }

    @Override
    public List<User> getTournamentParticipants(Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Error Occured"));

        return currentTournament.getParticipants();
    }

    @Override
    public Tournament updateUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        if (currentTournament.getCurrentSize() >= currentTournament.getSize()) {
            throw new Exception("Tournament is full");
        }
        if (!user.getCurrentTournaments().contains(currentTournament)) {
            user.addCurrentTournament(currentTournament);
        }
        if (!currentTournament.getParticipants().contains(user)) {
            currentTournament.addParticipant(user);
        }
        userRepository.save(user);
        currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        return tournamentRepository.save(currentTournament);
    }

    @Override
    public Tournament removeUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (user.getCurrentTournaments().contains(currentTournament)) {
            //System.out.println(currentTournament.getParticipants());
            user.removeCurrentTournament(currentTournament);
            //System.out.println(currentTournament.getParticipants().size());
            currentTournament.removeParticipant(user);
            currentTournament.setCurrentSize(currentTournament.getCurrentSize() - 1);
            userRepository.save(user);
        } else {
            System.out.println("User is not participating in the tournament");
            throw new Exception("User is not participating in the tournament");
        }
        return tournamentRepository.save(currentTournament); // Save and return the updated tournament
    }

    @Override
    public Tournament updateTournament(Long id, CreateTournamentRequest newTournament) throws Exception {
        return tournamentRepository.findById(id)
                .map(tournament -> {
                    if (newTournament.getTournament_name() != null) {
                        tournament.setTournament_name(newTournament.getTournament_name());
                    }
                    if (newTournament.getDate() != null) {
                        tournament.setDate(newTournament.getDate());
                    }
                    if (newTournament.getSize() != null) {
                        tournament.setSize(newTournament.getSize());
                    }
                    if (newTournament.getStatus() != null) {
                        tournament.setStatus(newTournament.getStatus());
                    }
                    if (newTournament.getNoOfRounds() != null) {
                        tournament.setNoOfRounds(newTournament.getNoOfRounds());
                    }
                    if (newTournament.getCurrentSize() != null) {
                        tournament.setCurrentSize(newTournament.getCurrentSize());
                    }
                    return tournamentRepository.save(tournament); // Save and return the updated tournament
                })
                .orElseThrow(() -> new Exception("Tournament not found")); // Throw exception if tournament does not
                                                                           // exist
    }

    @Override
    public List<Tournament> getTournamentsWithNoCurrentUser(Long userId) throws Exception {

        List<Tournament> list = getAllTournament();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        for (int i = 0; i < list.size(); i++) {
            Tournament tournament = list.get(i);
            if (tournament.getParticipants().contains(user)) {
                list.remove(tournament);
            }
        }

        return Optional.ofNullable(list).orElseGet(ArrayList::new);

    }

    @Override
    public Tournament createTournament(CreateTournamentRequest tournament) throws Exception {
        String tournament_name = tournament.getTournament_name();
        String date = tournament.getDate();
        String status = tournament.getStatus();
        Integer size = tournament.getSize();
        Integer noOfRounds = tournament.getNoOfRounds();
        Integer currentRounds = 1;
        // Tournament isEmailExist = tournamentRepository.findBy(email);
        // if (isEmailExist != null) {
        // System.out.println("Email Taken!");
        // throw new Exception("Email Is Already Used With Another Account");
        // }

        // if (userRepository.existsByUsername(username)){
        // System.out.println("Username Taken!");
        // throw new Exception("Username is already being used with another account");
        // }

       
        if (size % 2 != 0) {
            throw new Exception("Tournament size must be even");
        }
        
        Tournament createdTournament = new Tournament();
        createdTournament.setTournament_name(tournament_name);
        createdTournament.setDate(date);
        createdTournament.setStatus(status);
        createdTournament.setSize(size);
        createdTournament.setNoOfRounds(noOfRounds);
        createdTournament.setCurrentRound(currentRounds);

        return tournamentRepository.save(createdTournament);
    }

    @Override
    public List<User> getNonParticipatingCurrentUser(Long tournamentId) throws Exception {

        List<User> userList = userRepository.findAll();
        List<User> nonParticipatingUsers = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            List<Tournament> currentTournaments = user.getCurrentTournaments();
            if (user.getRole() != null && !user.getRole().equals("ROLE_ADMIN")) {
                boolean isValid = true;
                for (Tournament tour: currentTournaments) {
                    
                    if (tour.getId() - tournamentId == 0) {
                        
                        isValid=false;
                        break;
                    }
                }
                if (isValid){
                    nonParticipatingUsers.add(user);
                }
            }
       }
       return Optional.ofNullable(nonParticipatingUsers).orElseGet(ArrayList::new);
    }
    
    @Override
    public List<Tournament> getActiveTournament() {
        List<Tournament> list = getAllTournament();
        List<Tournament> activeTournaments = new ArrayList<>();
        for (Tournament tournament: list){
            System.out.println(tournament +" and " + tournament.getStatus());
            if (tournament.getStatus().equals("active")){
               activeTournaments.add(tournament);
            }
        }
       return activeTournaments;
    }

    @Override
    public List<Tournament> getOngoingTournament() {
        List<Tournament> list = getAllTournament();
        List<Tournament> inactiveTournaments = new ArrayList<>();  
        for (Tournament tournament: list){
            if (tournament.getStatus().equals("ongoing")){
                inactiveTournaments.add(tournament);
            }
        }
       return inactiveTournaments;
    }
    @Override
    public List<Tournament> getInactiveTournament() {
        List<Tournament> list = getAllTournament();
        List<Tournament> inactiveTournaments = new ArrayList<>();  
        for (Tournament tournament: list){
            if (tournament.getStatus().equals("completed")){
                inactiveTournaments.add(tournament);
            }
        }
       return inactiveTournaments;
    }

    @Override
    @Transactional(readOnly=true)
    public List<TournamentDTO> findAllTournamentsDTO() throws Exception {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return TournamentMapper.toDTOList(tournaments);
    }
    @Override
    public List<Tournament> getFilteredTournamentsByName() throws Exception {
       List<Tournament> list = getAllTournament();
       Collections.sort(list, new Comparator<Tournament>() {
        @Override
            public int compare(Tournament t1, Tournament t2) {
                return t1.getTournament_name().compareTo(t2.getTournament_name());
            }
        });
        return list;
    }
    @Override
    public List<Tournament> getFilteredTournamentsByDate() throws Exception {
        List<Tournament> list = getAllTournament();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Collections.sort(list, new Comparator<Tournament>() {
            @Override
            public int compare(Tournament t1, Tournament t2) {
                try {
                    Date date1 = dateFormat.parse(t1.getDate());
                    Date date2 = dateFormat.parse(t2.getDate());
                    return date2.compareTo(date1); // Sort in descending order
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return list;
    }
    @Override
    public List<Tournament> getFilteredTournamentsBySize() throws Exception {
        List<Tournament> list = getAllTournament();
    Collections.sort(list, new Comparator<Tournament>() {
        @Override
        public int compare(Tournament t1, Tournament t2) {
            int availableSlots1 = t1.getSize() - t1.getCurrentSize();
            int availableSlots2 = t2.getSize() - t2.getCurrentSize();
            return Integer.compare(availableSlots2, availableSlots1); // Sort in descending order
        }
    });
    return list;
    }

    // @Override
    // public List<TournamentDTO> findAllTournamentsDTO() throws Exception {
    //     return tournamentRepository.findAll().stream()
    //     .map(tournament -> new TournamentDTO(tournament.getId(),tournament.getTournament_name(),tournament.getParticipants(), tournament.getScoreboard(),tournament.getDate(),  tournament.getStatus(),tournament.getSize(), tournament.getCurrentSize(), tournament.getNoOfRounds(), tournament.getRounds()))
    //     .collect(Collectors.toList());
    // }

    @Override
    public Tournament startTournament(Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));
        if (!currentTournament.getStatus().equals("active")) {
            throw new Exception("Tournament is ongoing or completed");
        }

        // List<User> participants = currentTournament.getParticipants();

        Round firstRound = roundService.createFirstRound(id);
        // roundRepository.save(firstRound);

        List<Round> rounds = currentTournament.getRounds();
        rounds.add(firstRound);
        currentTournament.setStatus("ongoing");
        currentTournament.setRounds(rounds);
        // Map<Long, Double> oriScoreboard = firstRound.getScoreboard();
        // Map<Long, Double> newScoreboard = new HashMap<>();
        // for (Map.Entry<Long, Double> entry : oriScoreboard.entrySet()) {
        //     newScoreboard.put(entry.getKey(), entry.getValue());
        // }
        // currentTournament.setScoreboard(newScoreboard);
        
        Tournament newTournament = tournamentRepository.save(currentTournament);

        //debugging purposes
        logger.info("Tournament started");

        return newTournament;
    }

    @Override
    public void checkComplete(Long tournamentId) throws Exception {
        //debugging purposes
        logger.info("Tournament Check complete called");

        Tournament currentTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new Exception("Tournament not found"));

        //check if there are already the number of rounds specified
        if(currentTournament.getRounds().size() == currentTournament.getNoOfRounds()){
            //invoke endTournament, which will update the tournament status to completed
            //and get the final rankings (tiebreak and stuff)
            endTournament(currentTournament.getId());
        }
        else {
            //create the next round
            currentTournament.setCurrentRound(currentTournament.getCurrentRound() + 1);
            Round nextRound = roundService.createNextRound(currentTournament.getId());
            currentTournament.getRounds().add(nextRound);
            tournamentRepository.save(currentTournament);
        }
    }

    @Override
    public void endTournament(Long tournamentId) throws Exception{
        //debugging purposes
        logger.info("End tournament called");

        Tournament currentTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new Exception("Tournament not found"));
        
        currentTournament.setStatus("completed");
        tournamentRepository.save(currentTournament);
    }
    
        @Override
        @Transactional
        public void deleteTournament(Long id) throws Exception {
            try {
                Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));
        
        // Remove all participants from the tournament
                removeAllUsers(id);
        // Save the tournament to update the changes in the database
        tournamentRepository.save(tournament);
        
        // Now delete the tournament
        tournamentRepository.deleteById(id);
            } catch (Exception e) {
                throw e;
            }
        
        }
        @Override
        public void removeAllUsers(Long id) throws Exception {
            Tournament tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new Exception("Tournament not found"));
    
    // Remove all participants from the tournament
            List<User> participants = new ArrayList<>(tournament.getParticipants());
            for (User user : participants) {
                tournament.removeParticipant(user);
                user.removeCurrentTournament(tournament);
            }
            
            // Save the tournament to update the changes in the database
            tournamentRepository.save(tournament);
        }
}

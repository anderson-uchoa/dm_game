package eu.supersede.dm.ga.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ga.GAVirtualDB;
import eu.supersede.dm.ga.data.GAGame;
import eu.supersede.dm.iga.IGAAlgorithm;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;

@RestController
@RequestMapping("/garp/game")
public class GAGameRest
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ValutationCriteriaJpa valutationCriterias;

    @Autowired
    private RequirementsJpa availableRequirements;

    @Autowired
    private UsersJpa users;

    @RequestMapping(value = "/ownedgames", method = RequestMethod.GET)
    public List<GAGame> getOwnedGames(Authentication authentication)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        return GAVirtualDB.get().getOwnedGames(currentUser.getUserId());
    }

    @RequestMapping(value = "/activegames", method = RequestMethod.GET)
    public List<GAGame> getActiveGames(Authentication authentication)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        return GAVirtualDB.get().getActiveGames(currentUser.getUserId());
    }

    @RequestMapping(value = "/newrandom", method = RequestMethod.GET)
    public GAGame createNewRandomGame(Authentication authentication)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        Long userId = currentUser.getUserId();
        GAGame game = new GAGame();
        game.setId(System.currentTimeMillis());
        game.setOwner(userId);
        game.setDate("12/12/2016");
        game.setStatus("open");

        List<String> gameCriteria = new ArrayList<>();
        List<ValutationCriteria> criteria = valutationCriterias.findAll();
        Collections.shuffle(criteria, new Random(System.nanoTime()));

        for (int i = 0; ((i < 2) | i < criteria.size()); i++)
        {
            gameCriteria.add(criteria.get(i).getName());
        }

        List<Long> gameRequirements = new ArrayList<>();
        List<Requirement> requirements = availableRequirements.findAll();
        Collections.shuffle(requirements, new Random(System.nanoTime()));
        int max = new Random(System.currentTimeMillis()).nextInt(requirements.size());

        for (int i = 0; i < max; i++)
        {
            gameRequirements.add(requirements.get(i).getRequirementId());
        }

        List<Long> gameParticipants = new ArrayList<>();
        List<User> participants = users.findAll();
        Collections.shuffle(participants, new Random(System.nanoTime()));
        max = new Random(System.currentTimeMillis()).nextInt(participants.size());

        for (int i = 0; i < max; i++)
        {
            gameParticipants.add(participants.get(i).getUserId());
            log.info("Added user id " + participants.get(i).getUserId() + " to game id: " + game.getId());
        }

        GAVirtualDB.get().create(game, gameCriteria, gameRequirements, gameParticipants);
        return game;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public void submitPriorities(Authentication authentication, Long gameId, List<String> reqs)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        GAVirtualDB.get().setRanking(gameId, currentUser.getUserId(), reqs);
    }

    @RequestMapping(value = "/requirements", method = RequestMethod.POST)
    public List<Requirement> getRequirements(Authentication authentication, Long gameId)
    {
        return availableRequirements.findAll();
        // List<Requirement> gameReqs = new ArrayList<>();
        // List<Requirement> reqs = availableRequirements.findAll();
        // for( Long id : GAVirtualDB.get().getRequirements( gameId ) ) {
        //
        // }
        // return gameReqs;
    }

    @RequestMapping(value = "/calc", method = RequestMethod.GET)
    public List<Map<String, Double>> calcRanking(Authentication authentication, GAGame game)
    {
        IGAAlgorithm algo = new IGAAlgorithm();
        algo.setCriteria(GAVirtualDB.get().getCriteria(game));

        for (Long rid : GAVirtualDB.get().getRequirements(game.getId()))
        {
            algo.addRequirement("" + rid, new ArrayList<>());
        }

        List<Map<String, Double>> prioritization = algo.calc();
        return prioritization;
    }
}
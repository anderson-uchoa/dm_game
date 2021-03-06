package eu.supersede.gr.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.AHPGamesJpa;
import eu.supersede.gr.jpa.AHPGamesPlayersPointsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.model.HAHPGame;
import eu.supersede.gr.model.HAHPGamePlayerPoint;
import eu.supersede.gr.model.User;

@RestController
@RequestMapping("/ahprp/gameplayerpoint")
public class GamePlayerPointRest {
	
	@Autowired
    private AHPGamesPlayersPointsJpa gamesPlayersPoints;
	@Autowired
    private UsersJpa users;	
	@Autowired
    private AHPGamesJpa games;
	
	// get all the gamesPlayersPoints
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<HAHPGamePlayerPoint> getGamesPlayersPoints() {
		return gamesPlayersPoints.findAll();
	}
	
	// get all the gamesPlayersPoints for logged user
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public List<HAHPGamePlayerPoint> getUserGamesPlayersPoints(Authentication authentication) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		return gamesPlayersPoints.findByUser(user);
	}
	
	// get the gamesPlayersPoints of a specific user and game
	@RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
	public HAHPGamePlayerPoint getSpecificGameGamesPlayersPoints(Authentication authentication, @PathVariable Long gameId) {
		
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		HAHPGame game = games.findOne(gameId);
		
		return gamesPlayersPoints.findByUserAndGame(user, game);
	}	
}

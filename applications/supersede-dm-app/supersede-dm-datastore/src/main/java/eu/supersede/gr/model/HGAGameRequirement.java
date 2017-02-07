package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_ga_game_requirements")
public class HGAGameRequirement {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long			id;
    
	public Long		gameId;
	public Long		reqId;
	
}

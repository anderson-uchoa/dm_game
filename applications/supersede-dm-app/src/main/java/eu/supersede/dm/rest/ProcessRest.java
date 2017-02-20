/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eu.supersede.dm.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ActivityEntry;
import eu.supersede.dm.DMGame;
import eu.supersede.dm.DMLibrary;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.ProcessRole;
import eu.supersede.gr.jpa.ProcessesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

@RestController
@RequestMapping("processes")
public class ProcessRest
{
    @Autowired ProcessesJpa jpaProcesses;
    
    @Autowired RequirementsJpa jpaRequirements;
    
    @RequestMapping(value = "new", method = RequestMethod.POST)
    public Long newProcess()
    {
    	return DMGame.get().createEmptyProcess().getId();
    }
    
    static class JqxProcess {
    	public String id;
    	public String name;
    	public String state;
    	public String date;
    	public String objective;
    }
    
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<JqxProcess> getProcessList() {
    	List<JqxProcess> list = new ArrayList<JqxProcess>();
    	for( HProcess p : jpaProcesses.findAll() ) {
    		JqxProcess qp = new JqxProcess();
    		qp.id = "" + p.getId();
    		qp.name = p.getName();
    		qp.state = p.getStatus().name();
    		qp.objective = p.getObjective();
    		qp.date = p.getStartTime().toString();
    		list.add( qp );
    	}
    	return list;
    }
    
    public void addRequirements( Long procId, List<Long> reqList ) {
    	for( Long reqId : reqList ) {
    		Requirement r = jpaRequirements.findOne( reqId );
    		if( r == null ) continue;
    		if( r.getProcessId() != -1 ) continue;
    		r.setProcessId( procId );
    		jpaRequirements.save( r );
    	}
    }
    
    public void setRequirementsStatus( Long procId, List<Long> reqList, RequirementStatus status ) {
    	for( Long reqId : reqList ) {
    		Requirement r = jpaRequirements.findOne( reqId );
    		if( r == null ) continue;
    		RequirementStatus oldStatus = RequirementStatus.valueOf( r.getStatus() );
    		if( RequirementStatus.next( oldStatus ).contains( status ) ) {
    			r.setStatus( status.getValue() );
    			jpaRequirements.save( r );
    		}
    	}
    }
	
	@RequestMapping(value = "/available_activities", method = RequestMethod.GET)
	public List<ActivityEntry> getNextActivities( Long procId ) {
		return DMGame.get().getProcessStatus( procId ).findNextActivities( 
				DMLibrary.get().methods() );
	}
	
	@RequestMapping(value = "/users/import", method = RequestMethod.POST)
	public void importUsers( @RequestParam Long procId, @RequestParam List<Long> idlist ) {
		ProcessManager proc = DMGame.get().getProcessStatus( procId );
		if( idlist == null ) {
			return;
		}
		for( Long userid : idlist ) {
			proc.addProcessMember( userid, ProcessRole.User.name() );
		}
	}
	
//	@RequestMapping(value = "/users/import", method = RequestMethod.POST)
//	public void importUsers( @RequestParam Long procId, @RequestParam Long userid ) {
//		ProcessManager proc = DMGame.get().getProcessStatus( procId );
//		proc.addProcessMember( userid, ProcessRole.User.name() );
//	}
	
	@RequestMapping(value = "/users/list", method = RequestMethod.GET)
	public List<Long> getUserList( @RequestParam Long procId ) {
		ProcessManager proc = DMGame.get().getProcessStatus( procId );
		List<Long> list = new ArrayList<>();
		for( HProcessMember member : proc.getProcessMembers() ) {
			list.add( member.getId() );
		}
		return list;
	}
	
}

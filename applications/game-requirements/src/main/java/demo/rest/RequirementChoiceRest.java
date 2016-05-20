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

/**
* @author Andrea Sosi
**/

package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.RequirementsChoicesJpa;
import demo.model.RequirementChoice;

@RestController
@RequestMapping("/requirementchoice")
public class RequirementChoiceRest {

	@Autowired
    private RequirementsChoicesJpa requirementsChoices;
	
	// all the RequirementChoice
	@RequestMapping("")
	public List<RequirementChoice> getRequirementsChoices() {
		return requirementsChoices.findAll();
	}
}

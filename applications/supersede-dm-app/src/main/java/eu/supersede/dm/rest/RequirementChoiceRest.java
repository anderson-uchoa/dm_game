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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.gr.jpa.AHPRequirementsChoicesJpa;
import eu.supersede.gr.model.HAHPRequirementChoice;

@RestController
@RequestMapping("/requirementchoice")
public class RequirementChoiceRest
{
    @Autowired
    private AHPRequirementsChoicesJpa requirementsChoices;

    /**
     * Return all requirements choices.
     */
    @RequestMapping("")
    public List<HAHPRequirementChoice> getRequirementsChoices()
    {
        return requirementsChoices.findAll();
    }
}
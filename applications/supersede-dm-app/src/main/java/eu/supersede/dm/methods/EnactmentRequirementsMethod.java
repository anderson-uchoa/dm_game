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

package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMPhases;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.ProcessManager;

public class EnactmentRequirementsMethod implements DMMethod
{
    public static final String NAME = "Enact Requirements";
    public static final String PAGE = "enact_requirements";

    private String name;
    private List<DMRoleSpec> list;
    private List<DMOption> options;

    public EnactmentRequirementsMethod()
    {
        this.name = NAME;
        list = new ArrayList<>();
        options = new ArrayList<>();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public DMObjective getObjective()
    {
        return DMObjective.PrioritizeRequirements;
    }

    @Override
    public List<DMRoleSpec> getRoleList()
    {
        return list;
    }

    @Override
    public List<DMOption> getOptions()
    {
        return this.options;
    }

    @Override
    public List<DMCondition> preconditions()
    {
        List<DMCondition> list = new ArrayList<DMCondition>();

        list.add(new DMCondition()
        {
            @Override
            public boolean isTrue(ProcessManager mgr)
            {
                return mgr.getCurrentPhase().equals(DMPhases.TERMINATED);
            }
        });

        return list;
    }

    @Override
    public String getPage(ProcessManager mgr)
    {
        return PAGE;
    }

    @Override
    public String getDescription(ProcessManager mgr)
    {
        return NAME + " in process " + mgr.getProcess().getName();
    }

    @Override
    public String getLabel(ProcessManager arg0)
    {
        return NAME;
    }
}
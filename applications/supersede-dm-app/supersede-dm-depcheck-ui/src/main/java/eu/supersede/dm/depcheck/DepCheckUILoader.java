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

package eu.supersede.dm.depcheck;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class DepCheckUILoader
{
//    @Autowired
//    private ApplicationUtil au;

    @PostConstruct
    public void load()
    {
//        System.out.println("Registering DepCheck app");
//
//        Map<String, String> labels = new HashMap<>();
//        List<String> roles;
//
//        labels = new HashMap<>();
//        roles = new ArrayList<>();
//        labels.put("", "DepCheck Home");
//        roles.add("DM_ADMIN");
//        roles.add("OPINION_PROVIDER");
//        roles.add("DECISION_SCOPE_PROVIDER");
//        roles.add("OPINION_NEGOTIATOR");
//        au.addApplicationPage("supersede-dm-app", "depcheck/home", labels, roles);
    }
}
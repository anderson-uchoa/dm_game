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

package eu.supersede.gr.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.supersede.gr.model.HActivity;

public interface ActivitiesJpa extends JpaRepository<HActivity, Long>
{
    @Query("SELECT a FROM HActivity a WHERE processId = ?1 AND methodName = ?2")
    List<HActivity> findByProcessAndMethodName(Long processId, String methodName);

    @Query("SELECT a FROM HActivity a WHERE processId = ?1")
    List<HActivity> findByProcessId(Long processId);

    @Query("SELECT a FROM HActivity a WHERE userId = ?1")
    List<HActivity> findByUser(Long userId);
}
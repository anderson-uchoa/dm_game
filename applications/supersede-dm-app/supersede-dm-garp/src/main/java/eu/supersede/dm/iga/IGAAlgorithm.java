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

package eu.supersede.dm.iga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import eu.supersede.dm.iga.encoding.PrioritizationSolution;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.GAVariant;
import eu.supersede.dm.iga.problem.AbstractPrioritizationProblem.ObjectiveFunction;
import eu.supersede.dm.iga.problem.MultiObjectivePrioritizationProblem;
import eu.supersede.dm.iga.problem.SingleObjectivePrioritizationProblem;
import eu.supersede.dm.iga.utils.MapUtil;

public class IGAAlgorithm
{
    SortedMap<String, String[]> criteria = new TreeMap<>();
    Map<String, String> requirements = new HashMap<>();
    Map<String, Set<String>> dependencies = new HashMap<>();
    Map<String, Double> criteriaWeights = new HashMap<>();
    Map<String, Map<String, Double>> playerWeights = new HashMap<>();
    Map<String, Map<String, List<String>>> rankings = new HashMap<>();

    public void setCriteria(List<String> criteria)
    {
        this.criteria.clear();
        criteriaWeights.clear();

        for (String criterion : criteria)
        {
            System.out.println("Adding criterion: " + criterion);
            String[] criterionDetail = { criterion, "min" };
            this.criteria.put(criterion, criterionDetail);
            criteriaWeights.put(criterion, 1.0);
        }
    }

    public void setCriterionWeight(String criterion, Double w)
    {
        System.out.println("Setting weight for criterion " + criterion + ": " + w);
        criteriaWeights.put(criterion, w);
    }

    public void addRequirement(String req, List<String> deps)
    {
        System.out.println("Adding requirement: " + req);
        requirements.put(req, req);
        Set<String> list = dependencies.get(req);

        if (list != null)
        {
            list.clear();
        }
        else
        {
            list = new HashSet<>();
            dependencies.put(req, list);
        }

        list.addAll(deps);
    }

    public List<Map<String, Double>> calc()
    {
        ArrayList<Map<String, Double>> finalRanking = new ArrayList<>();
        double crossoverProbability = 0.9;
        CrossoverOperator crossover = new PMXCrossover(crossoverProbability);

        int searchBudget = 10000;
        int populationSize = 50;

        SolutionListEvaluator<PermutationSolution<?>> evaluator = new SequentialSolutionListEvaluator<>();
        SelectionOperator<List<PermutationSolution<?>>, PermutationSolution<?>> selection;

        AbstractGeneticAlgorithm<PermutationSolution<?>, ?> algorithm;
        MutationOperator<PermutationSolution<?>> mutation;
        AbstractPrioritizationProblem problem;

        ObjectiveFunction of = ObjectiveFunction.CRITERIA;
        GAVariant gaVariant = GAVariant.MO;

        if (gaVariant == GAVariant.MO)
        {
            problem = new MultiObjectivePrioritizationProblem(criteria, criteriaWeights, playerWeights, requirements,
                    dependencies, rankings, of, gaVariant);
        }
        else
        {
            problem = new SingleObjectivePrioritizationProblem(criteria, criteriaWeights, playerWeights, requirements,
                    dependencies, rankings, of, gaVariant);
        }

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        mutation = new PermutationSwapMutation(mutationProbability);

        if (gaVariant == GAVariant.MO)
        {
            selection = new BinaryTournamentSelection<>(
                    new RankingAndCrowdingDistanceComparator<PermutationSolution<?>>());
            algorithm = new NSGAII<PermutationSolution<?>>(problem, searchBudget, populationSize, crossover, mutation,
                    selection, evaluator);
            algorithm.run();
            List<PermutationSolution<?>> pareto = (List<PermutationSolution<?>>) algorithm.getResult();

            for (PermutationSolution<?> s : pareto)
            {
                finalRanking.add(MapUtil.sortByValue(((PrioritizationSolution) s).toRanks()));
            }
        }
        else
        {
            selection = new BinaryTournamentSelection<>(new ObjectiveComparator<PermutationSolution<?>>(0));
            algorithm = new GenerationalGeneticAlgorithm(problem, searchBudget, populationSize, crossover, mutation,
                    selection, evaluator);
            algorithm.run();
            PrioritizationSolution solution = (PrioritizationSolution) algorithm.getResult();
            System.out.println(solution.toNamedStringWithObjectives());
            finalRanking.add(MapUtil.sortByValue(solution.toRanks()));
        }

        return finalRanking;
    }

    /**
     * @param player: the player (name or some ID)
     * @param playerRanking: ranking of a player with respect to the various criteria
     */
    public void addRanking(String player, Map<String, List<String>> playerRanking)
    {
        for (String criterion : playerRanking.keySet())
        {
            System.out.println("Setting ranking for player " + player + " and criterion " + criterion + ": "
                    + Arrays.toString(playerRanking.get(criterion).toArray()));
        }

        this.rankings.put(player, playerRanking);
    }

    /**
     * @param criterion: name/ID of criterion
     * @param weights: Map<Player, Weight>
     */
    public void setPlayerWeights(String criterion, Map<String, Double> weights)
    {
        playerWeights.put(criterion, weights);

        for (String userId : weights.keySet())
        {
            System.out.println(
                    "Setting weight for player " + userId + " for criterion " + criterion + ": " + weights.get(userId));
        }
    }

    public void setDefaultPlayerWeights(List<String> criteria, List<String> players)
    {
        for (String criterion : criteria)
        {
            Map<String, Double> weights = new HashMap<>();

            for (String player : players)
            {
                weights.put(player, 1d);
            }

            setPlayerWeights(criterion, weights);
        }
    }
}
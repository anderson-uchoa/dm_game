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

var app = angular.module("w5app");

app.controllerProvider.register("select_solution", function($scope, $http, $location) {

    $scope.processId = $location.search().processId;
    $scope.activityId = $location.search().activityId;
    $scope.criteriaNames = {};
    $scope.currentOpinionProvider = {};
    $scope.rankings = {};

    var gameId;
    var gameRequirements = {};
    var gameStatus;
    var open = 'Open';
    var opinionProviders = [];

    $scope.currentPage = "page1";
    $scope.solutions = [];

    function loadPage() {
        $http.get('supersede-dm-app/garp/game/game?gameId=' + gameId)
        .success(function (data) {
            $scope.game = data;
            gameStatus = data.status;
            loadOpinionProviders();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function loadOpinionProviders() {
        $http.get('supersede-dm-app/garp/game/opinionproviders?gameId=' + gameId)
        .success(function (data) {
            opinionProviders = data;

            var source = {
                datatype: "json",
                datafields: [
                    { name: 'userId' },
                    { name: 'name' }
                ],
                localdata: data
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#opinion_providers").jqxComboBox({
                selectedIndex: 0,
                source: dataAdapter,
                displayMember: 'name',
                width: 400,
            });
            $scope.currentOpinionProvider = $("#opinion_providers").jqxComboBox('getSelectedItem').originalItem;

            loadCriteria();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function loadCriteria() {
        $http.get("supersede-dm-app/garp/game/gamecriteria?gameId=" + gameId)
        .success(function (data) {
            for (var i = 0; i < data.length; i++) {
                $scope.criteriaNames[data[i].criteriaId] = data[i].name;
            }
            loadRankings();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function loadRankings() {
        $http.get("supersede-dm-app/garp/game/ranking?gameId=" + gameId)
        .success(function (data) {
            $scope.rankings = data;

            if (!$scope.emptyRanking()) {
                loadRequirements();
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    function loadRequirements() {
        $http.get("supersede-dm-app/garp/game/gamerequirements?gameId=" + gameId)
        .success(function (data) {
            for (var i = 0; i < data.length; i++) {
                var requirementId = data[i].requirementId;
                gameRequirements[requirementId] = data[i];
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    function setCurrentPage(page) {
        $scope.currentPage = 'page' + page;
    }

    function getSolutions() {
        $scope.solutionReceived = false;
        $http.get("supersede-dm-app/garp/game/calc?gameId=" + gameId)
        .success(function (data) {
            $scope.solutionReceived = true;
            showSolutions(data);
        }).error(function(err){
            alert(err.message);
        });
    }

    function showSolutions(data) {
        var source = {
            datatype: "json",
            datafields: [
                { name: "requirements" },
                { name: "objectiveValues" },
                { name: "name" }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source, {
            autoBind: true,
            beforeLoadComplete: function (records) {
                var data = [];
                for (var i = 0; i < records.length; i++) {
                    var solution = records[i];
                    solution.name = "Solution " + (i + 1);
                    data.push(solution);
                }
                return data;
            }
        });
        $("#solutions").jqxComboBox({
            selectedIndex: 0,
            source: dataAdapter,
            displayMember: 'name',
            width: 400,
        });

        $scope.currentSolution = $("#solutions").jqxComboBox('getSelectedItem').originalItem;
        console.log("current solution:");
        console.log($scope.currentSolution);
        showObjectiveValues();
    }

    function showObjectiveValues() {
        var objectiveValues = [];

        for (var criterion in $scope.currentSolution.objectiveValues) {
            if ($scope.currentSolution.objectiveValues.hasOwnProperty(criterion)) {
                var record = {};
                record.criterion = $scope.criteriaNames[criterion];
                record.objectiveValue = $scope.currentSolution.objectiveValues[criterion];
                objectiveValues.push(record);
            }
        }

        var source = {
            datatype: "json",
            datafields: [
                { name: "criterion" },
                { name: "objectiveValue" }
            ],
            localdata: objectiveValues
        };
        console.log("source:");
        console.log(source);
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#objective_values").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Criterion', datafield: 'criterion' },
                { text: 'Objective Value', datafield: 'objectiveValue'}
            ]
        });
    }

    $scope.getRequirement = function(requirementId) {
        return gameRequirements[requirementId];
    };

    $scope.showSolutions = function () {
        getSolutions();
        setCurrentPage(2);
    };

    $scope.selectSolution = function () {
        $http({
            url: "supersede-dm-app/garp/game/solution",
            data: $scope.currentSolution.requirements,
            method: 'POST',
            params: {gameId : gameId}
        }).success(function() {
            $scope.home();
        }).error(function(err) {
            alert(err.message);
        });
    };

    $scope.selectSolutionAndStop = function () {
        $scope.selectSolution();

        $http.post('supersede-dm-app/garp/game/closegame?gameId=' + gameId + "&processId=" + $scope.processId)
        .success(function (data) {
            $scope.home();
        }).error(function (err) {
            alert(err.message);
        });
    };

    $scope.emptyRanking = function() {
        return Object.keys($scope.rankings).length === 0;
    };

    $scope.emptyRankingForOpinionProvider = function () {
            return $scope.rankings[$scope.currentOpinionProvider.userId] === undefined;
    };

    $scope.gameOpen = function () {
        return gameStatus == open;
    };

    $scope.canSelectSolution = function () {
        return $scope.gameOpen() && !$scope.emptyRanking();
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/home');
    };

    $http.get("supersede-dm-app/garp/game/id?processId=" + $scope.processId + "&activityId=" + $scope.activityId)
    .success(function (data) {
        gameId = data;
        loadPage();
    }).error(function (err) {
        alert(err.message);
    });

    $("#opinion_providers").on('select', function (event) {
        if (event.args) {
            var item = event.args.item;
            if (item) {
                $scope.currentOpinionProvider = item.originalItem;
                $scope.$apply();
            }
        }
    });

    $("#solutions").on('select', function (event) {
        if (event.args) {
            var item = event.args.item;
            if (item) {
                $scope.currentSolution = item.originalItem;
                $scope.$apply();
            }
        }
    });

    $("#jqx-loader").jqxLoader({ imagePosition: 'center', autoOpen: 'true', text: 'Computing solutions...'});
});
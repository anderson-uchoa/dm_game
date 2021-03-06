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

var app = angular.module('w5app');

app.controllerProvider.register('requirements_criterias_editing', function($scope, $http) {
	
	$scope.criterias = [];
	
	drawTabs = function() {
		console.log("Drawing");
		$('#jqxTabs').jqxTabs({ width: '100%' });
	    $('#unorderedList').css('visibility', 'visible');
	}
	
	getCriterias = function() {
		 $http.get('supersede-dm-assetmgr/criteria')
		 	.success(function(data) {
				$scope.criterias.length = 0;
				for(var i = 0; i < data.length; i++)
				{
					$scope.criterias.push(data[i]);
				}
				
				setupCriterias();
		 	});
	};
	
	criteriaToolbar = function (toolbar) {
        var me = this;
        var container = $("<div style='margin: 5px;'></div>");
        toolbar.append(container);
        container.append('<input id="addrowbutton" type="button" value="Add New Criteria" />');
        container.append('<input style="margin-left: 5px;" id="deleterowbutton" type="button" value="Delete Selected Criteria" />');
        container.append('<input style="margin-left: 5px;" id="updaterowbutton" type="button" value="Update Selected Criteria" />');
        $("#addrowbutton").jqxButton();
        $("#deleterowbutton").jqxButton();
        $("#updaterowbutton").jqxButton();
        // update row.
        $("#updaterowbutton").on('click', function () {
            var selectedrowindex = $("#criteriaGrid").jqxGrid('getselectedrowindex');
            editCriteria(selectedrowindex);
        });
        // create new row.
        $("#addrowbutton").on('click', function () {
        	createCriteria();
        });
        // delete row.
        $("#deleterowbutton").on('click', function () {
            var selectedrowindex = $("#criteriaGrid").jqxGrid('getselectedrowindex');
            deleteCriteria(selectedrowindex);
        });
    };
	
	setupCriterias = function() {
		//prepare criteria data
		var sourceCriteria =
		{
			datatype: "json",
			datafields: [
				{ name: 'name'},
				{ name: 'description'},
				{ name: 'criteriaId'}
			],
			id: 'criteriaId',
			localdata: $scope.criterias
		};
		var dataAdapterCriteria = new $.jqx.dataAdapter(sourceCriteria);
		$scope.criteriaSettings =
		{
			width: '100%',
			autoheight: true,
			pageable: true,
			editable: true,
			autorowheight: true,
			editmode: 'selectedrow',
			source: dataAdapterCriteria,
			columns: [
				{ text: 'Name', width: '30%', datafield: 'name' },
				{ text: 'Description', width: '70%', datafield: 'description' }
			],
			showtoolbar: true,
			rendertoolbar: criteriaToolbar
		};
		$scope.createWidgetCriteria = true;
		console.log("criteria");
		if($scope.createWidgetRequirement)
		{
			console.log("criteria drawing");
			drawTabs();
		}
	};
		
	getCriterias();
	
	createCriteria = function() {
		var criteria = {name: "", description: ""};
		$http({
			url: "supersede-dm-assetmgr/criteria/",
			data: criteria,
			method: 'POST'
		}).success(function(data, status, headers, config){
			var l = headers('Location');
			criteria.criteriaId = parseInt(l.substring(l.lastIndexOf("/") + 1));
			$('#criteriaGrid').jqxGrid('addrow', criteria.criteriaId, criteria);
		}).error(function(err){
		});
    };
	
	editCriteria = function(row) {
		$('#criteriaGrid').jqxGrid('endrowedit', row, false);
		var rowData = $('#criteriaGrid').jqxGrid('getrowdata', row);
		$http({
			url: "supersede-dm-assetmgr/criteria/",
			data: rowData,
			method: 'PUT'
		}).success(function(data){
		}).error(function(err){
		});
	};
	
	deleteCriteria = function(row) {
		var rowData = $('#criteriaGrid').jqxGrid('getrowdata', row);
		$http.delete('supersede-dm-assetmgr/criteria/' + rowData.criteriaId).success(function(data) {
			if(data == true){
				$('#criteriaGrid').jqxGrid('deleterow', $('#criteriaGrid').jqxGrid('getrowid', row));
			}
			else
			{
				alert("Can not delete criteria");
			}
		});
	};
	
	
	
	
	$scope.requirements = [];
	
	getRequirements = function () {
		$http.get('supersede-dm-assetmgr/requirement')
		.success(function(data) {
			$scope.requirements.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirements.push(data[i]);
			}
			
			setupRequirements();
		});	
	};
	
	requirementToolbar = function (toolbar) {
        var me = this;
        var container = $("<div style='margin: 5px;'></div>");
        toolbar.append(container);
        container.append('<input id="addrowbuttonrequirement" type="button" value="Add New Requirement" />');
        container.append('<input style="margin-left: 5px;" id="deleterowbuttonrequirement" type="button" value="Delete Selected Requirement" />');
        container.append('<input style="margin-left: 5px;" id="updaterowbuttonrequirement" type="button" value="Update Selected Requirement" />');
        $("#addrowbuttonrequirement").jqxButton();
        $("#deleterowbuttonrequirement").jqxButton();
        $("#updaterowbuttonrequirement").jqxButton();
        // update row.
        $("#updaterowbuttonrequirement").on('click', function () {
            var selectedrowindex = $("#requirementGrid").jqxGrid('getselectedrowindex');
            editRequirement(selectedrowindex);
        });
        // create new row.
        $("#addrowbuttonrequirement").on('click', function () {
        	createRequirement();
        });
        // delete row.
        $("#deleterowbuttonrequirement").on('click', function () {
            var selectedrowindex = $("#requirementGrid").jqxGrid('getselectedrowindex');
            deleteRequirement(selectedrowindex);
        });
    };
	
	setupRequirements = function() {
		//prepare requirement data
		var sourceRequirement =
		{
			datatype: "json",
			datafields: [
				{ name: 'name'},
				{ name: 'description'},
				{ name: 'requirementId'}
			],
			id: 'requirementId',
			localdata: $scope.requirements
		};
		var dataAdapterRequirement = new $.jqx.dataAdapter(sourceRequirement);
		$scope.requirementSettings =
		{
			width: '100%',
			autoheight: true,
			pageable: true,
			editable: true,
			autorowheight: true,
			editmode: 'selectedrow',
			source: dataAdapterRequirement,
			columns: [
			    { text: 'Name', width: '30%', datafield: 'name' },
			    { text: 'Description', width: '70%', datafield: 'description' }
			],
			showtoolbar: true,
			rendertoolbar: requirementToolbar
		};
		$scope.createWidgetRequirement = true;
		console.log("requirement");
		if($scope.createWidgetCriteria)
		{
			console.log("requirement drawing");
			drawTabs();
		}
	};
	
	getRequirements();
	
	createRequirement = function() {
		var requirement = {name: "", description: ""};
		$http({
			url: "supersede-dm-assetmgr/requirement/",
			data: requirement,
			method: 'POST'
		}).success(function(data, status, headers, config){
			var l = headers('Location');
			requirement.requirementId = parseInt(l.substring(l.lastIndexOf("/") + 1));
			$('#requirementGrid').jqxGrid('addrow', requirement.requirementId, requirement);
		}).error(function(err){
		});
	};
	
	editRequirement = function(row) {
		$('#requirementGrid').jqxGrid('endrowedit', row, false);
		var rowData = $('#requirementGrid').jqxGrid('getrowdata', row);
		$http({
			url: "supersede-dm-assetmgr/requirement/",
			data: rowData,
			method: 'PUT'
		}).success(function(data){
		}).error(function(err){
		});
	};
	
	deleteRequirement = function(row) {
		var rowData = $('#requirementGrid').jqxGrid('getrowdata', row);
		$http.delete('supersede-dm-assetmgr/requirement/' + rowData.requirementId).success(function(data) {
			if(data == true){
				$('#requirementGrid').jqxGrid('deleterow', $('#requirementGrid').jqxGrid('getrowid', row));
			}
			else
			{
				alert("Can not delete requirement");
			}
		});
	};
  
});
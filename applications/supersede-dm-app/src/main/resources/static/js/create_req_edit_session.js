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

app.controllerProvider.register('create_req_edit_session', function($scope, $http, $location) {

	$scope.processId = $location.search().processId;
	
	$scope.proceed = function() {
	    $http.post('supersede-dm-app/processes/requirements/edit/collaboratively?processId=' + $scope.processId)
        .success(function (data) {
            $location.url('supersede-dm-app/process?processId=' + $scope.processId);
        }).error(function (err) {
            alert(err.message);
		});
	};
	
	$scope.cancel = function() {
		$location.url('supersede-dm-app/process?processId=' + $scope.processId);
	};
	
	$scope.close = function() {
	    $http.post('supersede-dm-app/processes/requirements/edit/collaboratively?act=close&processId=' + $scope.processId)
        .success(function (data) {
			$location.url('supersede-dm-app/process?processId=' + $scope.processId);
        }).error(function (err) {
            alert(err.message);
        });
	};
	
	$scope.ongoingSession = function () {
	    return $scope.session === true;
	};
	
	$http.get('supersede-dm-app/processes/requirements/edit/collaboratively?processId=' + $scope.processId ).success(function(data) {
		$scope.session = (data.length > 0);
	});
});
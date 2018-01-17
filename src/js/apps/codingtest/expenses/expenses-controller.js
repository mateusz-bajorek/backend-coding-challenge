"use strict";

/******************************************************************************************

 Expenses controller

 ******************************************************************************************/
const app = angular.module("expenses.controller", []);

app.controller("ctrlExpenses", ["$rootScope", "$scope", "config", "restalchemy", function ExpensesCtrl($rootScope, $scope, $config, $restalchemy) {
	// Update the headings
	$rootScope.mainTitle = "Expenses";
	$rootScope.mainHeading = "Expenses";

	// Update the tab sections
	$rootScope.selectTabSection("expenses", 0);

	const restExpenses = $restalchemy.init({ root: $config.apiroot }).at("expenses");

	$scope.dateOptions = {
		changeMonth: true,
		changeYear: true,
		dateFormat: "dd/mm/yy"
	};

	const loadExpenses = function() {
		// Retrieve a list of expenses via REST
		restExpenses.get().then(function(expenses) {
			$scope.expenses = expenses;
		});
	};

	$scope.saveExpense = function() {
		$scope.errorMessage = "";
		if ($scope.expensesform.$valid) {
			// Post the expense via REST
			restExpenses.post($scope.newExpense)
				.error(function(data, status) {
					$scope.errorMessage = data.message;
				})
				.then(function() {
					// Reload new expenses list
					loadExpenses();
				});
		}
	};

	$scope.clearExpense = function() {
		$scope.newExpense = {};
	};

	$scope.calculateVat = function() {
		let amount = $scope.newExpense.amount;
		let vat;
		if (amount) {
			amount = amount.replace(/[^0-9.]+/g, "");
			vat = (parseFloat(amount) * 0.2).toFixed(2);
			vat = isNaN(vat) ? "00.00" : vat;
		}
		$scope.newExpense.vat = vat;
	};

	// Initialise scope variables
	loadExpenses();
	$scope.clearExpense();
}]);

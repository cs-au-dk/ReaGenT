if (!isTAJS) {
    if (isBrowser()) {
        setTimeout(function () {
            testStuff();
            setTimeout(function () {
                dumbMessages();
            });
        }, 100);
    } else {
        testStuff();
        setTimeout(function () {
            dumbMessages();
        }, 100);
    }

    function dumbMessages() {
        (function (assertionFailures, print) {
            var assertionsByPath = {};

            for (var i = 0; i < assertionFailures.length; i++) {
                var failure = assertionFailures[i];
                if (assertionsByPath[failure.path]) {
                    assertionsByPath[failure.path].push(failure);
                } else {
                    assertionsByPath[failure.path] = [failure];
                }
            }

            var paths = Object.keys(assertionsByPath).sort(function (a, b) {
                return a < b;
            });

            var createFailDescription = function (expected, actual, iteration) {
                var failDescription = path + ": (iteration: " + iteration + ")\n";
                failDescription += "    Here i expected: " + expected + ", but instead i got: \n";
                failDescription += "        typeof: " + typeof actual + "\n";
                try {
                    var string = JSON.stringify(actual + "");
                    failDescription += "        toString: " + string.substring(1, string.length - 1) + "\n";
                } catch (e) {
                    failDescription += "        toString: [ERROR] \n";
                }
                try {
                    var json = JSON.stringify(actual);
                    if (json.length < 200) {
                        failDescription += "        JSON: " + json + "\n";
                    } else {
                        failDescription += "        JSON: LONG!\n";
                    }
                } catch (e) {
                }
                // failDescription += "        sequence: " + failure.sequence.toString() + "\n";
                failDescription += "\n";
                return failDescription;
            };
            for (var i = 0; i < paths.length; i++) {
                var path = paths[i];

                var failures = assertionsByPath[path];
                var failStrings = [];
                var seen = new Set();
                for (var j = 0; j < failures.length; j++) {
                    var failure = failures[j];

                    var key = createFailDescription(failure.expected, failure.actual, 0);
                    if (seen.has(key)) {
                        continue;
                    }
                    seen.add(key);

                    var failDescription = createFailDescription(failure.expected, failure.actual, failure.iteration);
                    failStrings.push(failDescription);
                }

                for (var j = 0; j < failStrings.length; j++) {
                    print(failStrings[j]);
                }

                print(" ");
            }

            // var printedWarnings = [];
            // var printedErrors = [];
            if (printedWarnings.length > 0) {
                print("");
                print("---- WARNINGS ----");
                for (var i = 0; i < printedWarnings.length; i++) {
                    print(printedWarnings[i]);
                }
            }
            if (printedErrors.length > 0) {
                print("");
                print("---- ERRORS ----");
                for (var i = 0; i < printedErrors.length; i++) {
                    print(printedErrors[i]);
                }
            }

            if (runsWithCoverage) {
                if (isBrowser()) {
                    sendResultToChecker(JSON.stringify(__coverage__));
                } else {
                    printForReal(JSON.stringify(__coverage__));
                }
            } else {
                if (isBrowser()) {
                    sendResultToChecker(savedConsoleLog.join("\n"));
                } else {
                    // This is already handled just fine
                }
            }


        })(assertionFailures, print);
    }
}
/*{
 "path": path,
 "expected": expected,
 "actual": actual
 }*/

/*setTimeout(function () {
    dumbMessages();
    try {
        process.exit(0)
    } catch(e) {

    }
}, 100);*/

if (!isTAJS) {
    dumbMessages(); // TODO: TAJS Seems unhappy about async, so doing this for now.

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

            for (var i = 0; i < paths.length; i++) {
                var path = paths[i];

                var failures = assertionsByPath[path];
                var failStrings = new Set();
                for (var j = 0; j < failures.length; j++) {
                    var failure = failures[j];

                    var failDescription = path+ ":\n";
                    failDescription += "    Here i expected: " + failure.expected + ", but instead i got: \n";
                    var actual = failure.actual;
                    failDescription += "        typeof: " + typeof actual + "\n";
                    try {
                        failDescription += "        toString: " + actual + "\n";
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
                    } catch (e) { }
                    // failDescription += "        sequence: " + failure.sequence.toString() + "\n";
                    failDescription += "\n";
                    failStrings.add(failDescription);
                }

                failStrings = Array.from(failStrings);
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

        })(assertionFailures, print);
    }
}
/*{
 "path": path,
 "expected": expected,
 "actual": actual
 }*/

(function (assertionFailures, print) {
    assertionFailures = Array.from(assertionFailures);

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
        print(path+ ":");

        var failures = assertionsByPath[path];
        var failStrings = new Set();
        for (var j = 0; j < failures.length; j++) {
            var failure = failures[j];
            // TODO: Remove duplicate fails.

            var failDescription = "";
            failDescription += "    Here i expected: " + failure.expected + " but instead i got: \n";
            var actual = failure.actual;
            failDescription += "        typeof: " + typeof actual + "\n";
            failDescription += "        toString: " + actual + "\n";
            try {
                var json = JSON.stringify(actual);
                if (json.length < 200) {
                    failDescription += "        JSON: " + json + "\n";
                } else {
                    failDescription += "        JSON: LONG!\n";
                }
            } catch (e) { }
            failDescription += "\n";
            failStrings.add(failDescription);
        }

        failStrings = Array.from(failStrings);
        for (var j = 0; j < failStrings.length; j++) {
            print(failStrings[j]);
        }

        print(" ");
    }

})(assertionFailures, print);
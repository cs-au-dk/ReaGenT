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
        (function (print) {
            var shownWarnings = new Set();
            var shownErrors = new Set();
            if (printedWarnings.length > 0) {
                print("---- WARNINGS ----");
                for (var i = 0; i < printedWarnings.length; i++) {
                    var warning = printedWarnings[i];
                    if (!shownWarnings.has(warning)) {
                        print(warning);
                        shownWarnings.add(warning);
                    }
                }
            }
            if (printedErrors.length > 0) {
                print("---- ERRORS ----");
                for (var i = 0; i < printedErrors.length; i++) {
                    var error = printedErrors[i];
                    if (!shownErrors.has(error)) {
                        print(error);
                        shownErrors.add(error);
                    }
                }
            }

            if (runsWithCoverage) {
                printForReal(JSON.stringify(__coverage__));
            }
            if (isBrowser()) {
                printForReal("close");
            }


        })(print);
    }
}
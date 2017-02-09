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
            // var printedWarnings = [];
            // var printedErrors = [];
            if (printedWarnings.length > 0) {
                print("---- WARNINGS ----");
                for (var i = 0; i < printedWarnings.length; i++) {
                    print(printedWarnings[i]);
                }
            }
            if (printedErrors.length > 0) {
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
            }
            if (isBrowser()) {
                print("close");
            }


        })(print);
    }
}
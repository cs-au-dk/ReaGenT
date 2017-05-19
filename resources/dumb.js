if (isBrowser) {
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
        dumbCoverage();

        if (isBrowser) {
            printForReal("close");
        }


    })(print);
}
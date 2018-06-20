'use strict';

var preserveCamelCase = function preserveCamelCase(input) {
    var isLastCharLower = false;
    var isLastCharUpper = false;
    var isLastLastCharUpper = false;

    for (var i = 0; i < input.length; i++) {
        var c = input[i];

        if (isLastCharLower && /[a-zA-Z]/.test(c) && c.toUpperCase() === c) {
            input = input.slice(0, i) + '-' + input.slice(i);
            isLastCharLower = false;
            isLastLastCharUpper = isLastCharUpper;
            isLastCharUpper = true;
            i++;
        } else if (isLastCharUpper && isLastLastCharUpper && /[a-zA-Z]/.test(c) && c.toLowerCase() === c) {
            input = input.slice(0, i - 1) + '-' + input.slice(i - 1);
            isLastLastCharUpper = isLastCharUpper;
            isLastCharUpper = false;
            isLastCharLower = true;
        } else {
            isLastCharLower = c.toLowerCase() === c;
            isLastLastCharUpper = isLastCharUpper;
            isLastCharUpper = c.toUpperCase() === c;
        }
    }

    return input;
};

module.exports = function (input, options) {
    options = Object.assign({
        pascalCase: false
    }, options);

    var postProcess = function postProcess(x) {
        return options.pascalCase ? x.charAt(0).toUpperCase() + x.slice(1) : x;
    };

    if (Array.isArray(input)) {
        input = input.map(function (x) {
            return x.trim();
        }).filter(function (x) {
            return x.length;
        }).join('-');
    } else {
        input = input.trim();
    }

    if (input.length === 0) {
        return '';
    }

    if (input.length === 1) {
        return options.pascalCase ? input.toUpperCase() : input.toLowerCase();
    }

    if (/^[a-z\d]+$/.test(input)) {
        return postProcess(input);
    }

    var hasUpperCase = input !== input.toLowerCase();

    if (hasUpperCase) {
        input = preserveCamelCase(input);
    }

    input = input.replace(/^[_.\- ]+/, '').toLowerCase().replace(/[_.\- ]+(\w|$)/g, function (m, p1) {
        return p1.toUpperCase();
    });

    return postProcess(input);
};
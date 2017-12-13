/*

export module module {
    function callToString1(x: {foo: true}): string;
    function callToString2(x: object): string;
    function callToString3(x: () => void): string;
}*/

function callToString(x) {
    return x.toString();
}

module.exports = {
    callToString1: callToString,
    callToString2: callToString,
    callToString3: callToString
};
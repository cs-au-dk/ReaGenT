/*

export module module {
    // All of these return a <div>
    function foo(): HTMLElement;
    function bar(): HTMLDivElement;
    function baz(): HTMLSpanElement; // <- error!
}*/

function returnDiv() {
    return document.createElement("div");
}

module.exports = {
    foo: returnDiv,
    bar: returnDiv,
    baz: returnDiv
};
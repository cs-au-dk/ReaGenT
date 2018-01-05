/*
export module module {
    function num(): 123;
    function str(): "foo";
    function bool(): true;
}*/

function num() {
    return Math.random() * 1000000 - Math.random() * 1000000;
}
function str() {
    return num() + "foo" + num();
}
function bool() {
    return Math.random() > 0.5;
}

module.exports = {
    num: num,
    str: str,
    bool: bool
};
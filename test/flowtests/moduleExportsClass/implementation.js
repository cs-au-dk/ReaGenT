/*
declare module bizniz {
    declare class Bizniz {
        constructor(): Bizniz;
        foo(): true;
        static bar(): true
    }
    declare module exports = typeof Bizniz;
}
*/

function Bizniz() {
    this.foo = function () {
        return false; // <- Wrong
    }
}
Bizniz.bar = function () {
    return false; // <- Wrong
}

module.exports = Bizniz;
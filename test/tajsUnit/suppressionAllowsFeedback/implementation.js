/*
export module module {

    export class C1 {
        x1: String
    }

    class C2 {
        x2: String
    }

    class C3 {
        x3: String
    }

    class C4 {
        x4: String
    }

    export function fun(): C2;

    export let property: C3;

    export function callback(f: (C4) => void);

    function useC1(x: C1);
    function useC2(x: C2);
    function useC3(x: C3);
    function useC4(x: C4);
}*/


function C1() { this.x1 = 5; }
function C2() { this.x2 = undefined; }
function C3() { this.x3 = function(){}; }
function C4() { this.x4 = {}; }

function fun() {
    return new C2();
}

function callback(f) {
    f(new C4());
}

module.exports = {
    C1: C1,
    fun: fun,
    property: new C3(),
    callback: callback,
    useC1: function(x) {},
    useC2: function(x) {},
    useC3: function(x) {},
    useC4: function(x) {}
};
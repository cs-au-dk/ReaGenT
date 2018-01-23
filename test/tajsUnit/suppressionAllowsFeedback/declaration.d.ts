export module module {

    export class C1 {
        x1: String
    }

    interface C2 {
        x2: String
    }

    interface C3 {
        x3: String
    }

    interface C4 {
        x4: String
    }

    export function fun(): C2;

    export let property: C3;

    export function callback(f: (x:C4) => void): void;

    function useC1(x: C1): void;
    function useC2(x: C2): void;
    function useC3(x: C3): void;
    function useC4(x: C4): void;
}
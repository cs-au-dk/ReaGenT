
// all of this is correct
declare module foo {
    function number(): number;
    function number123(): 123;
    function bool(): boolean;
    function TRUE(): true;
    function FALSE(): false;
    function str(): string;
    function undef(): undefined;
    function never(): never;
    function NULL(): null;
    function foo(): "foo"
}
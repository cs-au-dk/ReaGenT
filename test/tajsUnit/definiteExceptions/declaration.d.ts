
export module module {
    function foo00(): string; // <- returns bool | string (maybe error).
    function foo0(): string; // <- returns bool (definite error).
    function foo1(): string | number; // <- returns bool (definite error).
    function foo2(): string | boolean; // <- returns bool | number (maybe error).
    function foo3(): {foo: boolean} // returns {foo: string} (definite error)
    function foo4(): {foo: boolean} // returns {foo: string | number} (maybe error)
    function foo5(): {foo: boolean} // returns {foo: string} | {foo: string} (maybe error)
}
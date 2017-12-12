
export module module {
    class F {
        constructor(value: number);
        value: number;
    }
    function getHiddenValue(f: F): true;
    function getHiddenValueFromNested(f: F | {f: {test: true}}): true;
}
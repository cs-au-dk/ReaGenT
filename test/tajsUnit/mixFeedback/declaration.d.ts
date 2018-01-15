
export module module {
    class K {
        constructor(v: number);
        value: number;
    }
    function id(x: K[]): true; // fails when feedback value is used.
}
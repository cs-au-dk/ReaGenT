
export module module {
    class F {
        constructor(value: number);
        value: number;
        feedback: boolean;
    }
    function crashesOnConstructed(f: F): true;
    function crashesOnFeedback(f: F): true;

}
/*

export module module {
    class F {
        constructor(value: number);
        value: number;
        feedback: boolean;
    }
    function crashesOnConstructed(f: F): true;
    function crashesOnFeedback(f: F): true;

}*/

module.exports = {
    F: function (value) {
        this.value = value;
        this.test = true; // <- private.
        this.feedback = false;
    },
    crashesOnConstructed: function (f) {
        if (!f.test) {
            throw new Error();
        }
        return true;
    },
    crashesOnFeedback: function (f) {
        if (f.test) {
            throw new Error();
        }
        return true;
    }
};
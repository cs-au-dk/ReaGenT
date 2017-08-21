/*interface Foo<T> {
    value: T;
}

export module module {
    function foo(str: Foo<string>, num: Foo<number>): true;
}*/

module.exports = {
    foo: function (strValue, numValue) {
        if (arguments.length !== 2) {
            return "called with wrong length";
        }
        if (typeof strValue !== "object") {
            return "first arg is not object"
        }
        if (typeof numValue !== "object") {
            return "second arg is not object"
        }
        if (typeof strValue.value !== "string") {
            return strValue.value;
        }
        if (typeof numValue.value !== "number") {
            return numValue;
        }
        return true;
    }
};
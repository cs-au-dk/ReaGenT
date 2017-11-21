/*interface Foo {
    [index: string]: number | string;
    str: string;
    num: number

}

export module module {
    function foo(a: Foo, index: string): true;
}*/

module.exports = {
    foo: function (obj, index) {
        if (typeof obj.str !== "string") {
            return obj.str;
        }
        if (typeof obj.num !== "number") {
            return obj.num;
        }

        var value = obj[index];
        if (typeof value === "undefined") {
            return true;
        }
        if (typeof value === "string") {
            return true;
        }
        if (typeof value === "number") {
            return true;
        }
        return true;
    }
};
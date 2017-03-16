/*
interface MyFunction {
    (a: number) : number;
    (a: string) : string;
}
export module foo {
    function twice(
        a: number | string | boolean,
        b: MyFunction
): string;
}*/

module.exports = {
    twice: function (num, c) {
        return c(num) + c(num) + "";
    }
};
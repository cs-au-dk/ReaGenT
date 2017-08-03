/*
declare class Foo {
    num: number;
    getNum(): number;
    setNum(num: string): void; // <- this is the error.
}*/

module.exports = function () {
    this.num = 123;
    this.getNum = function () {
        return this.num;
    };
    this.setNum = function (num) {
        this.num = num;
    };
};
/*// all here is correct
interface Foo {
    new (): {
        bar: true
    }
}

declare function foo(f: Foo): true;*/

module.exports = function foo (f) {
    var res = new f();
    return res.bar;
};
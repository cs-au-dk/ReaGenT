/*interface FooBar {
    value: 2,
    foo: {
        bar: FooBar
    }
}

export function foo(): FooBar;*/

var fooBar = {};
fooBar.value = 2;
fooBar.foo = {
    bar: fooBar
};

module.exports = function (x) {
    return fooBar;
};
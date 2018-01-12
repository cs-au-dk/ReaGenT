/*
interface MyCollection<T> {
    get(i: number): T | undefined;
    add(t: T): void;
    values: T[]
}


export module module {
    var foo: MyCollection<number>;
    var bar: MyCollection<string>;
    var baz: MyCollection<boolean>;
}*/

function MyCollection() {
    this.values = [];
}
MyCollection.prototype.add = function (t) {
    this.values.push(t);
};
MyCollection.prototype.get = function (i) {
    return this.values[i];
};

var foo = new MyCollection();
foo.add(123);
foo.add(321);
var bar = new MyCollection();
bar.add("foo");
bar.add("bar");
var baz = new MyCollection();
baz.add(true);
baz.add(false);

module.exports = {
    foo: foo,
    bar: bar,
    baz: baz
};
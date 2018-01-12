/*

export module module {
    function foo(x: Function): void;
}*/

module.exports = {
    foo: function (x) {
        x("foo");
        x(1);
        x(2);
        x();
        x(3);
        x(3,54,6,7,2,3,4,6,7,8,false,true,"foo");
        x(true);
        x(false);
        x({f: "true"});
    }
};
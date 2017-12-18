/*

export module module {
    function foo(): void;
}*/

// console.log(Object.keys(module));
// console.log(Object.keys(exports));

console.log(exports === module.exports)

exports.foo = function () {

};

console.log(exports);
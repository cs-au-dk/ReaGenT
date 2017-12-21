/*
export function useBar(foo: { bar: true; }): true; // uses constructed
*/

module.exports = function useBar(foo) {
    return foo.bar;
};
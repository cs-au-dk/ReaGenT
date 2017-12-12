/*export module module {
    function getLength(a: () => void): number;
    function getName(a: () => void): string;
    function getPrototype(a: () => void): object;
    function getPrototypeConstructor(a: () => void): Function;
    function protypeConstructorFunctionItself(a: () => void): true;
    function instanceOfFunction(a: () => void): true;
}*/

module.exports = {
    getLength: function (obj) {
        return obj.length;
    },
    getName: function (obj) {
        return obj.name;
    },
    getPrototype: function (obj) {
        var prototype = obj.prototype;
        return prototype;
    },
    getPrototypeConstructor: function (obj) {
        var prototype = obj.prototype;
        var constructor = prototype.constructor;
        return constructor;
    },
    protypeConstructorFunctionItself: function (obj) {
        return obj.prototype.constructor === obj;
    },
    instanceOfFunction: function (obj) {
        return obj instanceof Function;
    }
};
/*export module module {
    function getLength(a: () => void): number;
    function getName(a: () => void): string;
    function instanceOfFunction(a: () => void): true;
}*/

module.exports = {
    getLength: function (obj) {
        return obj.length;
    },
    getName: function (obj) {
        return obj.name;
    },
    instanceOfFunction: function (obj) {
        return obj instanceof Function;
    }
};
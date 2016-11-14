var id = function (func) {
    var result = func();
    return {
        value: result,
        duplicateFlag: true
    };
};
module.exports = {
    id1: id,
    id2: id
};
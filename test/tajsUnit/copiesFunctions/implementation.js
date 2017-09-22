function copy(x) {
    var result = {};
    for (var key in x) {
        result[key] = x[key];
    }
    return result;
}

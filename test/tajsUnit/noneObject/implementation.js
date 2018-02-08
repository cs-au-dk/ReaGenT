function createStore(reducer, enhancer) {
    if (typeof enhancer !== 'undefined') {
        return enhancer(createStore)(reducer);
    }

    var action = {type: 'foo'};
    reducer(action);
    return {};
}

function getUnexpectedStateShapeWarningMessage(action) {
    action.type === 'bar';
}

function combineReducers(reducer) {
    return function (action) {
        getUnexpectedStateShapeWarningMessage(action);
        reducer(action);
    };
}

module.exports.createStore = createStore;
module.exports.combineReducers = combineReducers;

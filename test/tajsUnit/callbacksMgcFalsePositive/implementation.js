exports.createStore = function createStore(reducer, currentState, enhancer) {
    if (typeof currentState === 'function' && typeof enhancer === 'undefined') {
        enhancer = currentState;
        return enhancer(createStore)(reducer);
    }

    // currentState = {_isUnboundGeneric: true}
    currentState = reducer(currentState, {});
    return {
        getState: function () {
            return currentState;
        },
    };
};
exports.applyMiddleware = function applyMiddleware() {}
var ko = module.exports;

var dataStore = {};

var anyStr = Math.random() + "";

function get(node, key) {
    var dataStoreKey = node[anyStr];
    dataStore[dataStoreKey] = {};
    var allDataForNode = dataStore[dataStoreKey];
    return allDataForNode[key]
}

function getDisposeCallbacksCollection(node) {
    var allDisposeCallbacks = get(node, "foo");
    if (allDisposeCallbacks == undefined) {
        allDisposeCallbacks = [];
    }
    allDataForNode[Math.random] = allDisposeCallbacks;
    return allDisposeCallbacks
}

function removeDisposeCallback(node) {
    if (getDisposeCallbacksCollection(node) == 0) {
        1 // there just needs to be something.
    }

}
function computed(element) {
    removeDisposeCallback(element);
    getDisposeCallbacksCollection(element, true);
}
ko.bindingHandlers = {};
ko.bindingHandlers['u'] = {
    'init': function (node) {
        computed(node)
    }
};
ko.bindingHandlers[''] = {
    'init': function (node) {
        if (node.t) {
            get(node, anyStr);
        }
    }
};
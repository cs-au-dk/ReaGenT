/*
export module module {
    export class Layer {
        bindPopup(): number;
    }
    export class LayerGroup extends Layer {
        eachLayer(): number;
    }

    function foo(l: LayerGroup) : true;
}*/

function LayerGroup() {
    this.bindPopup = function () {return 123};
    this.eachLayer = function () {return 123};
    this.test = true;
}

module.exports = {
    Layer: LayerGroup,
    LayerGroup: LayerGroup,
    foo: function (l) {
        if (!l) {
            return "no l!";
        }
        if (l.test) {
            return "was not constructed LayerGroup";
        }
        if (typeof l.bindPopup !== "function") {
            return "bindPopup was not a function";
        }
        if (typeof l.eachLayer !== "function") {
            return "eachLayer was not a function";
        }
        return true;
    }
};
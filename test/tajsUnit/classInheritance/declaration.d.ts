
export module module {
    export class Layer {
        bindPopup(): number;
    }
    export class LayerGroup extends Layer {
        eachLayer(): number;
    }

    function foo(l: LayerGroup) : true;
}
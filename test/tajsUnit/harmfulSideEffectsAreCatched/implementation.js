/*
export module module {
    export let marker: true;
    function writeFalseToMarker(): void;
}*/

var exports = {
    marker: true,
    writeFalseToMarker: function (x) {
        exports.marker = false;
    }
};
module.exports = exports;
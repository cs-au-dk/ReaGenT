function IntroJs() {
    this._targetElement = document.createElement("div");
}
function _populateHints() {
    document.createElement('a').onclick = _hideHint.bind(this, 123);
    document.createElement('a').onclick = (function () {}).bind(this, "string");
};
function _hideHint(stepId) {
    this._targetElement.querySelector(stepId + '"]');
    this._hintCloseCallback.call(this, stepId);
};
var introJs = function () {
    return new IntroJs();
};
introJs.fn = IntroJs.prototype = {
    onhintclose: function(providedCallback) {
        this._hintCloseCallback = providedCallback;
    },
    addHints: function() {
        _populateHints.call(this);
    }
};
exports.introJs = introJs;
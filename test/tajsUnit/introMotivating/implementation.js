var introJs = function () {
    return new IntroJs(document.body);
};
function IntroJs(obj) {
    this._targetElement = obj;
}
IntroJs.prototype = {
    onhintclose: function(providedCallback) {
        this._hintCloseCallback = providedCallback;
    },
    hideHints: function () {
        var hints = this._targetElement.querySelectorAll('.introjs-hint');
        for (var i = 0; i < hints.length; i++) {
            this._hintCloseCallback.call(this, hints[i].getAttribute('data-step'));

            // the fixed version.
            // this._hintCloseCallback.call(this, parseInt(hints[i].getAttribute('data-step')));
        }
    }
};
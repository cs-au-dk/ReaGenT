function IntroJs() {}
function _populateHints() {
    var introItems = [];
    var hints = document.body.querySelectorAll('*[data-hint]');
    var i, l;
    for (i = 0, l = hints.length; i < l; i++) {
        introItems.push({});
    }
    for (i = 0, l = introItems.length; i < l; i++) {
        var item = introItems[i];
        this._hintClickCallback(item);
    }
};
var introJs = function () {
    return new IntroJs();
};
introJs.fn = IntroJs.prototype = {
    onhintclick: function(providedCallback) {
        this._hintClickCallback = providedCallback;
    },
    addHints: function() {
        _populateHints.call(this);
    }
};
exports.introJs = introJs;
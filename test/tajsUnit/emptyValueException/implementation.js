(function(window, document, exportName, undefined) {
    function inherit(child, base, properties) {
        var baseP = base.prototype,
        childP = child.prototype = Object.create(baseP);
        if (properties) {
            Object.assign(childP, properties);
        }
    }
    function Recognizer(options) {
        this.options = Object.assign({}, this.defaults, options || {});
    }
    function TapRecognizer() {
        Recognizer.apply(this, arguments);
    }
    inherit(TapRecognizer, Recognizer, {});
    function Hammer(element, options) {}
    Object.assign(Hammer, {
        Tap: TapRecognizer,
        inherit: inherit
    });
    window.Hammer = Hammer;
})(window, document, 'Hammer');
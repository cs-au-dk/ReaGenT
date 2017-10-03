var Path = {
    map: function (path) {
        return new Path.route(path);
    },
    dispatch: function (passed_route) {
        Path.previous = Path.current;
        Path.current = passed_route;
        Path.defined[Path.previous].do_exit();
    },
    route: function (path) {
        Path.defined[path] = this;
        this.exit = function (fn) {
            this.do_exit = fn;
            return this;
        }
    },
    defined: {}
};
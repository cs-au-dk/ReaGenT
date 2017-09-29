var Path = {
    map: function (path) {
        return new Path.route(path);
    },
    dispatch: function (passed_route) {
        Path.routes.previous = Path.routes.current;
        Path.routes.current = passed_route;
        Path.routes.defined[Path.routes.previous].do_exit();
    },
    route: function (path) {
        Path.routes.defined[path] = this;
        this.exit = function (fn) {
            this.do_exit = fn;
            return this;
        }
    },
    routes: {
        defined: {}
    }
};
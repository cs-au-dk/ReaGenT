function b2Vec2() {

}

var Box2D = {
    Collision: {
        Shapes: {}
    },
    Dynamics: {
        Joints: {}
    }
};
(function () {
    function emptyFn() {};
    function inherit (cls) {
        cls.prototype = new emptyFn;
    }

    function b2CircleShape() {}
    function b2PolygonShape() {}
    Box2D.Collision.Shapes.b2PolygonShape = b2PolygonShape;
    function b2DistanceJoint() {}
    inherit(b2CircleShape);
    inherit(b2PolygonShape);
    b2PolygonShape.AsArray = function () {
        return new b2PolygonShape();
    };
    b2PolygonShape.prototype.ComputeSubmergedArea = function () {};
    inherit(b2DistanceJoint);
})();
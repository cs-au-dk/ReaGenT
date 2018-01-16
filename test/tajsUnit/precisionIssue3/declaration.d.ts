declare class b2Vec2 {}
declare namespace Box2D.Collision.Shapes {
    export class b2PolygonShape {
        public static AsArray(vertices: Box2D.Common.Math.b2Vec2[], vertexCount?: number): b2PolygonShape;
        public ComputeSubmergedArea(): number;
    }
}
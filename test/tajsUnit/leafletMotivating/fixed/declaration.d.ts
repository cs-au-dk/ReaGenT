// Type definitions for Leaflet.js 1.0
// Project: https://github.com/Leaflet/Leaflet
// Definitions by: Alejandro Sánchez <https://github.com/alejo90>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped



declare namespace L {

    export module LineUtil {
        export function simplify(points: Point[], tolerance: number): Point[];

        export function pointToSegmentDistance(p: Point, p1: Point, p2: Point): number;

        export function closestPointOnSegment(p: Point, p1: Point, p2: Point): Point;
    }

    export type PointTuple = [number, number];

    export class Point {
        constructor(x: number, y: number, round?: boolean);
        // constructor(coords: PointTuple | {x: number, y: number});
        // clone(): Point;
        // add(otherPoint: PointExpression): Point; // investigate if this mutates or returns a new instance

        // subtract(otherPoint: PointExpression): Point;
        divideBy(num: number): Point;
        multiplyBy(num: number): Point;
        // scaleBy(scale: PointExpression): Point;
        // unscaleBy(scale: PointExpression): Point;
        round(): Point;
        floor(): Point;
        ceil(): Point;
        // distanceTo(otherPoint: PointExpression): number;
        // equals(otherPoint: PointExpression): boolean;
        // contains(otherPoint: Point): boolean;
        toString(): string;
        x: number;
        y: number;
    }

    type PointExpression = Point | PointTuple;

}

declare module 'leaflet' {
    export = L;
}

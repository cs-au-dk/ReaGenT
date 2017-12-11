declare namespace L {
    export function closestPointOnSegment(p: Point): Point;
    export class Point {
        constructor(x: number);
        multiplyBy(): Point;
        x: number;
    }
}
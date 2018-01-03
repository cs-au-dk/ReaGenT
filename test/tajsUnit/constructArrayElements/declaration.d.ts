declare namespace L {
    export function id(points: Point[]): Point[];
    export class Point {
        constructor();
        equals(x: number | string): boolean;
    }
}
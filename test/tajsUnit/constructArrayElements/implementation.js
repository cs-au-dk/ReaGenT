/*
declare namespace L {
    export function id(points: Point[]): Point[];
    export class Point {
        constructor(x: number, y: number);
        equals(otherPoint: number | string): boolean;
    }
}
 */
function Point() {
    this.equals = function () {
        return true;
    }
}

window.L = {
    id: function (points) {
        return points;
    },
    Point: Point
};
declare module module {
    export class b2Shape {
        public Set(other: b2Shape): void;
    }

    export class b2CircleShape extends b2Shape {
        constructor(radius: number); // TODO: For some reason the parameter is needed for the test to fail, and that doesn't make any sense.
        public GetRadius(radius: number): void;
        public Set(other: b2CircleShape): void;
    }

}

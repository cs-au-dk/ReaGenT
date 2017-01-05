declare module module {
    export class b2Shape {
        public Set(other: b2Shape): void;
    }

    export class b2CircleShape extends b2Shape {
        constructor(radius?: number);
        public GetRadius(): void;
        public Set(other: b2CircleShape): void;
    }

}

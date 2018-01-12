declare class Sortable {
    constructor(element: any);

    static utils: {
        css(element: any, prop: string): void;
    };

    toArray(): void;
}
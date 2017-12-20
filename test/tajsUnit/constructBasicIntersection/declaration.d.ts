
interface Options {
    value: true;
}

declare class Foo<T extends Options> {
    constructor(options: T);
    value: true;
}
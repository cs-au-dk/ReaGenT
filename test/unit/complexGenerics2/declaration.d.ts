declare module module {
    class Bluebird<R> {
        nodeify(callback: (err: any, value?: R) => void): this;
        static each<R>(): Bluebird<R[]>;
    }
}
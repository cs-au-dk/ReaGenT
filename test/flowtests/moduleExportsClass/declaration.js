declare module bizniz {
    declare class Bizniz {
        constructor(): Bizniz;
        foo(): true;
        static bar(): true
    }
    declare module exports = typeof Bizniz;
}
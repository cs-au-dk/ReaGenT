declare module "double-ended-queue" {
    declare class Deque<T> {
        length: number,

        constructor<T>(capacity: void): Deque<T>,
    }

    declare module.exports: Class<Deque>
}

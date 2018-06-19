declare module "yada" {
    declare export interface Base<T> {
        value: T;
    }

    declare type Alias<R> = Base<R>;

    declare var str: Alias<string>;
    declare var str2: Base<string>;
    declare var num: Alias<number>;
    declare var num2: Base<number>;
}

declare module Cookies {
    declare type CookieOptions = {
        expires?: number | Date,
        path?: string,
        domain?: string,
        secure?: boolean
    }
    declare type ConverterFunc = (value: ?string, name: string) => string;
    declare type ConverterObj = {
        read: ConverterFunc,
        write: ConverterFunc
    };
    declare interface Cookie {
        defaults: CookieOptions;
        set(name: string, value: mixed, options?: CookieOptions): void;
        get(...args: Array<void>): { [key: string]: string };
        // get(name: string, ...args: Array<void>): string | void;
        remove(name: string, options?: CookieOptions): void;
        getJSON(name: string): mixed;
        withConverter(converter: ConverterFunc | ConverterObj): this;
        noConflict?: () => this; //  <- only defined in browser version.
    }

    declare module.exports: Cookie;
}


declare interface NumeralJSLocale {
    delimiters: {
        thousands: string,
        decimal: string
    },
    abbreviations: {
        thousand: string,
        million: string,
        billion: string,
        trillion: string
    },
    ordinal(num: number): string,
    currency: {
        symbol: string
    }
}
declare type RoundingFunction = (value: string/*number*/) => number;
declare interface NumeralJsFormat {
    regexps: {
        format: RegExp,
        unformat: RegExp
    },
    format(value: any, format: string, roundingFunction: RoundingFunction): string,
    unformat(value: string): number
}
declare type RegisterType = 'format' | 'locale';
declare class Numeral {
    constructor(value?: any): Numeral,
    static version: string,
    static isNumeral(x: any): boolean,

    /**
     * This function sets the current locale.  If no arguments are passed in,
     * it will simply return the current global locale key.
     */
    static locale(key?: string): string,

    /**
     * Registers a language definition or a custom format definition.
     * @param what Allowed values are: either 'format' or 'locale'
     * @param key The key of the registerd type, e.g. 'de' for a german locale definition
     * @param value The locale definition or the format definitiion
     */
    static register(
        what: RegisterType,
        key: string,
        value: NumeralJSLocale | NumeralJsFormat): NumeralJSLocale | NumeralJsFormat,
    static zeroFormat(format: string): void,
    static nullFormat(format: string): void,
    static defaultFormat(format: string): void,
    clone(): Numeral,
    format(inputString?: string, roundingFunction?: RoundingFunction): string,
    // formatCurrency(inputString?: string): string,
    // unformat(inputString: string): number,
    value(): ?number,
    // valueOf(): number, // no valudOf overload defined. So it returns a Numeral, which there is no reason to declare.
    set(value: any): Numeral,
    add(value: any): Numeral,
    subtract(value: any): Numeral,
    multiply(value: any): Numeral,
    divide(value: any): Numeral,
    difference(value: any): number,
    static validate(value: any, culture: any): boolean
}

declare var numeral: Class<Numeral>;

declare module 'numeral' {
    declare module.exports: Class<Numeral>
}

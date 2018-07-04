declare module 'he' {
    declare type encodeOptions = {
        useNamedReferences?: bool,
        decimal?: bool,
        encodeEverything?: bool,
        strict?: bool,
        allowUnsafeSymbols?: bool,
    };
    declare type decodeOptions = {
        isAttributeValue?: bool,
        strict?: bool,
    };
    declare module.exports: {
        version: string,
        encode: (text: string, options?: encodeOptions) => string,
        decode: (text: string, options?: decodeOptions) => string,
        escape(text: string): string,
        unescape(text: string, options?: encodeOptions): string,
    }
}

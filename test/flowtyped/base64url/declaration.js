type buffer$NonBufferEncoding =
    'hex' | 'HEX' |
    'utf8' | 'UTF8' | 'utf-8' | 'UTF-8' |
    'ascii' | 'ASCII' |
    'binary' | 'BINARY' |
    'base64' | 'BASE64' |
    'ucs2' | 'UCS2' | 'ucs-2' | 'UCS-2' |
    'utf16le' | 'UTF16LE' | 'utf-16le' | 'UTF-16LE' | 'latin1';
type buffer$Encoding = buffer$NonBufferEncoding | 'buffer'

type Buffer = {};

declare module 'base64url' {
    declare module.exports: {
        (value: string | Buffer, encoding?: buffer$Encoding): string;
        encode(value: string | Buffer, encoding?: buffer$Encoding): string;
        decode(value: string, encoding?: buffer$Encoding): string;
        toBuffer(value: string): Buffer;
        fromBase64(value: string): string;
        toBase64(value: string): string;
    };
}

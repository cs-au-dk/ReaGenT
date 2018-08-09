declare module SemVer {
    export function parse(v: string | SemVer): SemVer;
    export class SemVer {}
}
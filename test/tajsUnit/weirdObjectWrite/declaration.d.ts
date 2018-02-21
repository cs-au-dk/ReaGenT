declare module SemVer {
    export class SemVer {
        constructor(version: SemVer);
        constructor(version: string);
        raw: string;
    }
}

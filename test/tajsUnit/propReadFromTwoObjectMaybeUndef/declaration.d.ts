declare module SemVer {
    export function major(v: string | SemVer): number;
    export class SemVer {
        major: number
    }
}
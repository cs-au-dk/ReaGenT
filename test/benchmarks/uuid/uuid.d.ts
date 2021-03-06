// Uses ArrayLike to admit Unit8 and co.
type OutputBuffer = ArrayLike<number>;// | Buffer;

interface V1Options {
    node?: number[];
    clockseq?: number;
    msecs?: number | Date;
    nsecs?: number;
}

type V4Options = {random: number[]} | {rng(): number[]};

type v1String = (options?: V1Options) => string;
type v1Buffer = <T extends OutputBuffer>(options: V1Options | null | undefined, buffer: T, offset?: number) => T;
type v1 = v1String & v1Buffer;

type v4String = (options?: V4Options) => string;
type v4Buffer = <T extends OutputBuffer>(options: V4Options | null | undefined, buffer: T, offset?: number) => T;
type v4 = v4String & v4Buffer;

type v5String = (name: string | number[], namespace: string | number[]) => string;
type v5Buffer = <T extends OutputBuffer>(name: string | number[], namespace: string | number[], buffer: T, offset?: number) => T;
type v5 = v5String & v5Buffer;

interface UuidStatic {
    v1: v1;
    v4: v4;
    // v5: v5; // apparently not in the browserified version
}

declare const uuid: UuidStatic & v4;
export = uuid;

declare module "uuid-js" {
    declare class UUID<V: number> {
        fromParts(
            timeLow: mixed,
            timeMid: mixed,
            timeHiAndVersion: mixed,
            clockSeqHiAndReserved: mixed,
            clockSeqLow: mixed,
            node: mixed
        ): mixed;
        hex: ?string;
        toBytes(): Array<mixed>;
        toString(): ?string;
        toURN(): string;
        // version: V; // False positive! Resulting from how generics are instantiated and checked in TSTest.
        static create(version?: 4): UUID<4>;
        static create(version: 1): UUID<1>;
        static firstFromTime(time: number): UUID<1>;
        static fromBinary(binary: mixed): ?UUID<*>;
        static fromBytes(bytes: number[]): ?UUID<*>;
        static fromURN(string: string): ?UUID<*>;
        static lastFromTime(time: number): UUID<1>;
    }

    declare module.exports: Class<UUID>;
}

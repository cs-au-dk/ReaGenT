declare module 'deep-diff' {
    declare type Difference =
        // Path became optional because of the "if (path && path.length) {" statement in the library, the path field is only written if there is a path of a non-zero length, and zero-length paths can happen. (Concretely observed on all kinds).
        | {kind: "N", path?: Array<string | number>, rhs: mixed}
        | {kind: "D", path?: Array<string | number>, lhs: mixed} // I saw lhs not being defined if this was part of an Kind="A". But since it is mixed, it doesn't matter.
        | {kind: "E", path?: Array<string | number>, lhs: mixed, rhs: mixed}
        | {kind: "A", path?: Array<string | number>, index: number, item: Difference}
    declare type PrefilterFn = (path: Array<string | number>, key: string | number) => bool|void // The key can be a number, look e.g. at the line "deepDiff(lhs[i], rhs[i], changes, prefilter, currentPath, i, stack, orderIndependent);". The I is the key, and it is a number. The path can also contain numbers, but I'm not sure how that happens.
    declare module.exports: {
        (lhs: mixed, rhs: mixed, prefilter?: PrefilterFn, acc?: Array<Difference>): ?Array<Difference>,
        diff(lhs: mixed, rhs: mixed, prefilter?: PrefilterFn, acc?: Array<Difference>): ?Array<Difference>,
        observableDiff(lhs: mixed, rhs: mixed, observerFn: Function): void,
        applyChange(lhs: mixed, rhs: mixed, difference: Difference): void,
        revertChange(lhs: mixed, rhs: mixed, difference: Difference): void
    }
}

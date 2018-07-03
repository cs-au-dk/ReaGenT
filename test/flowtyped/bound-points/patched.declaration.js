// @flow

declare module "bound-points" {
    declare module.exports: {
        (points: []) : [[], []];
        (points: Array<[number, number]>) : [[number, number], [number, number]];
    }
}

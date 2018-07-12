declare module "nanoevents" {
    declare class NanoEvents {
        constructor(): NanoEvents,

        on(event: string, cb: (x?: any) => void): void,

        emit(event: string, data?: any): void
    }
    declare module.exports: Class<NanoEvents>;
}

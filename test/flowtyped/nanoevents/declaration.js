declare module "nanoevents" {
    declare class NanoEvents {
        constructor(): NanoEvents,

        on(event: string, cb: (any) => void): void,

        emit(event: string, data?: any): void
    }
    declare module.exports: Class<NanoEvents>;
}

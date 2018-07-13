declare module 'signals' {
    declare class SignalBinding {
        active: boolean;
        context: ?mixed;
        params: ?mixed[];
        constructor(signal: Signal, listener: Function, isOnce: boolean, listenerContext: ?mixed, priority: ?number): void;
        detach(): ?Function;
        execute(paramsArr: ?mixed[]): mixed;
        // benign: are undefined after the destroy() method has been called.
        // getListener(): Function;
        // getSignal(): Signal;
        isBound(): boolean;
        isOnce(): boolean;
        toString(): string;
    }
    declare class Signal {
        active: boolean;
        memorize: boolean;
        VERSION: string;
        constructor(): void;
        add(listener: Function, listenerContext: ?mixed, priority: ?number): SignalBinding;
        addOnce(listener: Function, listenerContext: ?mixed, priority: ?number): SignalBinding;
        dispatch(...params: mixed[]): void;
        dispose(): void;
        forget(): void;
        getNumListeners(): number;
        halt(): void;
        has(listener: Function, context: ?mixed): boolean;
        remove(listener: Function, context: ?mixed): () => void;
        removeAll(): void;
        toString(): string;
    }

    declare module.exports: typeof Signal;
}

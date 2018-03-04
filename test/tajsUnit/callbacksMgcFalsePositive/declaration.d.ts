declare module redux {
    export type Reducer<S> = (state: S, action: any) => S;
    export interface Store<S> {
        getState(): S;
    }
    export interface StoreCreator {
        <S>(reducer: Reducer<S>, enhancer: StoreEnhancer): Store<S>;
    }
    export type StoreEnhancer= <S>(next: StoreEnhancerStoreCreator<S>) => StoreEnhancerStoreCreator<S>;
    export type StoreEnhancerStoreCreator<S> = (reducer: Reducer<S>) => Store<S>;
    export const createStore: StoreCreator;
    export function applyMiddleware(): StoreEnhancer;
}
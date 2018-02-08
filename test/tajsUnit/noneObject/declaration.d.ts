declare module redux {
    export type Reducer = (action: {type: string} | {type: string}) => {};
    export function combineReducers(reducers: Reducer): Reducer;
    export type StoreEnhancer = (next: StoreEnhancerStoreCreator) => StoreEnhancerStoreCreator;
    export type StoreEnhancerStoreCreator = () => void;
    export const createStore: {
        (reducer: Reducer, enhancer?: StoreEnhancer): void;
    };
}
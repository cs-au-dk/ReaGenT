declare module redux {
    export interface ActionCreatorsMapObject {
        [key: string]: () => void;
    }

    export function bindActionCreators<M extends ActionCreatorsMapObject>(actionCreators: M): M;
}
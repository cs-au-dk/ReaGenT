declare module 'credit-card-type' {
    declare interface ICardType {
        niceType: string,
        type: string,
        gaps: (number | null)[],
        lengths: (number | null)[],
        code: {
            name: string,
            size: number | null
        }
    }

    declare interface ICardTypeInput extends ICardType {
        prefixPattern?: RegExp,
        exactPattern?: RegExp,
    }

    declare type ILibrary = {
        (cardNumber: string): ICardType[];
        types: {
            [key: string]: string // simple mistake. There is an internal object that has the type that was originally declared. But this types object is a single string->string.
        };
        getTypeInfo: (type: string) => ICardType | null;
        removeCard: (name: string) => void;
        addCard: (config: ICardTypeInput) => void;
        changeOrder: (name: string, position: number) => void;
        resetModifications: () => void;
    }

    declare module.exports: ILibrary
}

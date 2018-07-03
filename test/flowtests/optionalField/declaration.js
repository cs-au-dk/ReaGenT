declare module foo {
    declare type ICardType = {
        prefixPattern?: string
    }

    declare type ILibrary = {
        (cardNumber: string): ICardType;
    }

    declare module.exports: ILibrary
}

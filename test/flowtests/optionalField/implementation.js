/*
declare module foo {
    declare type ICardType = {
        prefixPattern?: string
    }

    declare type ILibrary = {
        (cardNumber: string): ICardType;
    }

    declare module.exports: ILibrary
}
*/

function Base() {
    this.base = true;
}
function Extended() {
    this.base = true;
    this.extended = true;
}

module.exports = function (cardNumber) {
    return { // <- This is OK.

    }
}
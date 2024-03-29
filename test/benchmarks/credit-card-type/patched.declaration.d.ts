// Type definitions for Credit Card Type v5.0.0
// Project: https://github.com/braintree/credit-card-type
// Definitions by: Karol Janyst <https://github.com/LKay>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped

declare namespace creditCardType {

    type CardBrand = "american-express" | "diners-club" | "discover" | "jcb" | "maestro" | "master-card" | "unionpay" | "visa"

    interface CreditCardTypeInfo {
        niceType?: string
        type?: CardBrand
        prefixPattern?: string
        exactPattern?: string
        gaps?: Array<number>
        lengths?: Array<number>
        code?: {
            name?: string
            size?: number
        }
    }

    interface CreditCardType {
        (number: string): Array<CreditCardTypeInfo>
        getTypeInfo (type: string): CreditCardTypeInfo | null
        types: { [type: string]: string }
    }
}

declare const creditCardType: creditCardType.CreditCardType
export = creditCardType

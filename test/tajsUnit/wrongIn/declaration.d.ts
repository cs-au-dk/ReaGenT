declare var Please: PleaseJS.Please;

declare namespace PleaseJS{
    export interface Please {
        NAME_to_HEX(name: string): string | undefined;
    }
}

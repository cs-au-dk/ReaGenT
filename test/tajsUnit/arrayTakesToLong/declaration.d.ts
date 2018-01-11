interface IAccountingStatic {
    formatNumber(number: number[], precision?: number, thousand?: string, decimal?: string): void;
}
declare var accounting: IAccountingStatic;
declare module "accounting" {
    export = accounting;
}
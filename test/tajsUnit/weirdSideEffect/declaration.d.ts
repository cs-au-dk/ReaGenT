declare var Please: PleaseJS.Please;
declare namespace PleaseJS{
    export interface Please{
        make_color(): void;
        HSV_to_RGB(hsv: HSV): RGB;
    }
    export interface RGB{
        r: number;
    }
    export interface HSV{
        v: number;
    }
}
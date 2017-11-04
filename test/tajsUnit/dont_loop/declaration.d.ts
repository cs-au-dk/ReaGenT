declare var Please: PleaseJS.Please;
declare namespace PleaseJS{
    export interface Please{
        RGB_to_HEX(rgb: RGB): string;
        HSV_to_RGB(hsv: HSV): RGB;
        HSV_to_HEX(hsv: HSV): string;
    }
    export interface RGB{}
    export interface HSV{}
}
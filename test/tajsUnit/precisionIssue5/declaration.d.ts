declare var Please: {
    HSV_to_RGB(hsv: HSV): RGB;
    RGB_to_HSV(rgb: RGB): HSV;
};
interface RGB{}
interface HSV{
    h: number;
}
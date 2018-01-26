var Please = {};
Please.HSV_to_RGB = function(HSV){
    var v = HSV.v;
    return {
        r: v
    };
};
Please.RGB_to_HSV = function( ){
    return {
        v: 0
    };
};
Please.make_color = function(){
    Please.HSV_to_RGB({v: undefined});
};
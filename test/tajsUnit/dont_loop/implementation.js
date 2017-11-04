(function( globalName, root, factory ) {
	if ( typeof define === 'function' && define.amd ) {}
	else{
		root[globalName] = factory();
	}
}('Please', this, function(){
	function define_Please(){
		var Please = {};
		Please.RGB_to_HEX = function( RGB ){
			return (37).toString( 16 ).slice( 1 );
		};
		Please.HSV_to_RGB = function( HSV ){
			return {};
		};
		Please.HSV_to_HEX = function( HSV ){
			return Please.RGB_to_HEX( Please.HSV_to_RGB( HSV ));
		};
		return Please;
	}
	return define_Please();
}));
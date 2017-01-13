declare namespace module {
    export interface FeatureGroup {
        setStyle(style: {}): this;
    }
    export interface GeoJSON extends FeatureGroup {
        setStyle(style: () => {}): this;
    }
    var foo: GeoJSON;
}
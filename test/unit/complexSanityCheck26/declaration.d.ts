// TODO: This is the reason Polymer soundnesstest fails.
declare namespace foobar {
    type PropConstructorType = DateConstructor | ArrayConstructor;
    interface PropObjectType {
        type: PropConstructorType;
    }
}
declare const olli: foobar.PropObjectType;
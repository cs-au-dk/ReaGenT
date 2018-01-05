declare var axios: {
    request: () => void;
};
declare module "axios" {
    export = axios;
}
declare namespace lunr
{
    class Store {}
    class TokenStore {
        root:{[token:string]:TokenStore};
    }
}
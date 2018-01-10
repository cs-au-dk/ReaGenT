/*
interface PDFPromise<T> {}
interface PDFSource {
    httpHeaders?: any;
}
interface PDFProgressData {}
interface PDFDocumentProxy {}
interface PDFJSStatic {
    getDocument(
        pdfDataRangeTransport?: any,
        progressCallback?: (progressData: PDFProgressData) => void)
        : PDFPromise<PDFDocumentProxy>;
    getDocument(
        source: PDFSource,
        progressCallback?: (progressData: PDFProgressData) => void)
        : PDFPromise<PDFDocumentProxy>;
}
declare var PDFJS: PDFJSStatic;*/

module.exports = {
    getDocument: function (x) {
        return x;
    },
    foo: function (x) {
        return x;
    },
    bar: function (x) {
        return x;
    }
};
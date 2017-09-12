interface PDFPromise<T> {
    (onResolve: {foo: T}): PDFPromise<boolean>;
}
declare var PDFJS: PDFPromise<string>;
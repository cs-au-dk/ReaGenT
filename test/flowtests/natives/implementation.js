/*
declare module 'left-pad' {
  declare module.exports: (elem: HTMLTextAreaElement) => true;
}
 */
module.exports = function (elem) {
    return elem instanceof HTMLTextAreaElement;
}
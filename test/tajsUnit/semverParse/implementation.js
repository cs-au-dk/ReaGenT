exports.parse = parse;
function parse(version) {
    if (version instanceof SemVer)
        return version;
    return new SemVer();
}
exports.SemVer = SemVer;
function SemVer() {}
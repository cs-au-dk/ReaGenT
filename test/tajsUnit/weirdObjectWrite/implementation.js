exports.SemVer = SemVer;
function SemVer(version) {
    if (version instanceof SemVer) {
        if (Math.random() > 0.5)
            return version;
        else
            version = version.raw;
    }
    this.raw = version;
}
function SemVer(version) {
    if (version instanceof SemVer) {
        return version;
    }
    this.major = 2;
}

module.exports = {
    SemVer: SemVer,
    major: function (a) {
        var result = new SemVer(a);
        var major = result.major;
        return major;
    }
};

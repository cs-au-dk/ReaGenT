

function Text(data) {
    this.value = data;
}

Text.prototype = {
    asText: function TextAsText() {
        "Text";
        return this.value;
    }
}

function Num(data) {
    this.value = data;
}

Num.prototype = {
    asText: function NumAsText() {
        "Num";
        return this.value.toString();
    }
}

module.exports = {
    Fragments: {
        Text: Text,
        Number: Num,
    },
};
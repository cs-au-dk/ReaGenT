declare module 'vscode' {

    /**
     * The version of the editor.
     */
    declare export var version: string;

    /**
     * Represents a reference to a command. Provides a title which
     * will be used to represent a command in the UI and, optionally,
     * an array of arguments which will be passed to the command handler
     * function when invoked.
     */
    declare export interface Command {
        /**
         * Title of the command, like `save`.
         */
        title: string;

        /**
         * The identifier of the actual command handler.
         * @see [commands.registerCommand](#commands.registerCommand).
         */
        command: string;

        /**
         * Arguments that the command handler should be
         * invoked with.
         */
        arguments?: any[];
    }

    /**
     * Represents a line of text, such as a line of source code.
     *
     * TextLine objects are __immutable__. When a [document](#TextDocument) changes,
     * previously retrieved lines will not represent the latest state.
     */
    declare export interface TextLine {

        /**
         * The zero-based line number.
         *
         * @readonly
         */
        lineNumber: number;

        /**
         * The text of this line without the line separator characters.
         *
         * @readonly
         */
        text: string;

        /**
         * The range this line covers without the line separator characters.
         *
         * @readonly
         */
        range: Range;

        /**
         * The range this line covers with the line separator characters.
         *
         * @readonly
         */
        rangeIncludingLineBreak: Range;

        /**
         * The offset of the first character which is not a whitespace character as defined
         * by `/\s/`.
         *
         * @readonly
         */
        firstNonWhitespaceCharacterIndex: number;

        /**
         * Whether this line is whitespace only, shorthand
         * for [TextLine.firstNonWhitespaceCharacterIndex](#TextLine.firstNonWhitespaceCharacterIndex]) === [TextLine.text.length](#TextLine.text.length).
         *
         * @readonly
         */
        isEmptyOrWhitespace: boolean;
    }

    /**
     * Represents a text document, such as a source file. Text documents have
     * [lines](#TextLine) and knowledge about an underlying resource like a file.
     */
    declare export interface TextDocument {

        /**
         * The associated URI for this document. Most documents have the __file__-scheme, indicating that they
         * represent files on disk. However, some documents may have other schemes indicating that they are not
         * available on disk.
         *
         * @readonly
         */
        uri: Uri;

        /**
         * The file system path of the associated resource. Shorthand
         * notation for [TextDocument.uri.fsPath](#TextDocument.uri.fsPath). Independent of the uri scheme.
         *
         * @readonly
         */
        fileName: string;

        /**
         * Is this document representing an untitled file.
         *
         * @readonly
         */
        isUntitled: boolean;

        /**
         * The identifier of the language associated with this document.
         *
         * @readonly
         */
        languageId: string;

        /**
         * The version number of this document (it will strictly increase after each
         * change, including undo/redo).
         *
         * @readonly
         */
        version: number;

        /**
         * true if there are unpersisted changes.
         *
         * @readonly
         */
        isDirty: boolean;

        /**
         * Save the underlying file.
         *
         * @return A promise that will resolve to true when the file
         * has been saved.
         */
        save(): Thenable<boolean>;

        /**
         * The number of lines in this document.
         *
         * @readonly
         */
        lineCount: number;

        /**
         * Returns a text line denoted by the line number. Note
         * that the returned object is *not* live and changes to the
         * document are not reflected.
         *
         * @param line A line number in [0, lineCount).
         * @return A [line](#TextLine).
         */
        lineAt(line: number): TextLine;

        /**
         * Returns a text line denoted by the position. Note
         * that the returned object is *not* live and changes to the
         * document are not reflected.
         *
         * The position will be [adjusted](#TextDocument.validatePosition).
         *
         * @see [TextDocument.lineAt](#TextDocument.lineAt)
         * @param position A position.
         * @return A [line](#TextLine).
         */
        lineAt(position: Position): TextLine;

        /**
         * Converts the position to a zero-based offset.
         *
         * The position will be [adjusted](#TextDocument.validatePosition).
         *
         * @param position A position.
         * @return A valid zero-based offset.
         */
        offsetAt(position: Position): number;

        /**
         * Converts a zero-based offset to a position.
         *
         * @param offset A zero-based offset.
         * @return A valid [position](#Position).
         */
        positionAt(offset: number): Position;

        /**
         * Get the text of this document. A substring can be retrieved by providing
         * a range. The range will be [adjusted](#TextDocument.validateRange).
         *
         * @param range Include only the text included by the range.
         * @return The text inside the provided range or the entire text.
         */
        getText(range?: Range): string;

        /**
         * Get a word-range at the given position. By default words are defined by
         * common separators, like space, -, _, etc. In addition, per languge custom
         * [word definitions](#LanguageConfiguration.wordPattern) can be defined.
         *
         * The position will be [adjusted](#TextDocument.validatePosition).
         *
         * @param position A position.
         * @return A range spanning a word, or `undefined`.
         */
        getWordRangeAtPosition(position: Position): Range;

        /**
         * Ensure a range is completely contained in this document.
         *
         * @param range A range.
         * @return The given range or a new, adjusted range.
         */
        validateRange(range: Range): Range;

        /**
         * Ensure a position is contained in the range of this document.
         *
         * @param position A position.
         * @return The given position or a new, adjusted position.
         */
        validatePosition(position: Position): Position;
    }

    /**
     * Represents a line and character position, such as
     * the position of the cursor.
     *
     * Position objects are __immutable__. Use the [with](#Position.with) or
     * [translate](#Position.translate) methods to derive new positions
     * from an existing position.
     */
    declare export class Position {

        /**
         * The zero-based line value.
         * @readonly
         */
        line: number;

        /**
         * The zero-based character value.
         * @readonly
         */
        character: number;

        /**
         * @param line A zero-based line value.
         * @param character A zero-based character value.
         */
        constructor(line: number, character: number): void;

        /**
         * Check if `other` is before this position.
         *
         * @param other A position.
         * @return `true` if position is on a smaller line
         * or on the same line on a smaller character.
         */
        isBefore(other: Position): boolean;

        /**
         * Check if `other` is before or equal to this position.
         *
         * @param other A position.
         * @return `true` if position is on a smaller line
         * or on the same line on a smaller or equal character.
         */
        isBeforeOrEqual(other: Position): boolean;

        /**
         * Check if `other` is after this position.
         *
         * @param other A position.
         * @return `true` if position is on a greater line
         * or on the same line on a greater character.
         */
        isAfter(other: Position): boolean;

        /**
         * Check if `other` is after or equal to this position.
         *
         * @param other A position.
         * @return `true` if position is on a greater line
         * or on the same line on a greater or equal character.
         */
        isAfterOrEqual(other: Position): boolean;

        /**
         * Check if `other` equals this position.
         *
         * @param other A position.
         * @return `true` if the line and character of the given position are equal to
         * the line and character of this position.
         */
        isEqual(other: Position): boolean;

        /**
         * Compare this to `other`.
         *
         * @param other A position.
         * @return A number smaller than zero if this position is before the given position,
         * a number greater than zero if this position is after the given position, or zero when
         * this and the given position are equal.
         */
        compareTo(other: Position): number;

        /**
         * Create a new position relative to this position.
         *
         * @param lineDelta Delta value for the line value, default is `0`.
         * @param characterDelta Delta value for the character value, default is `0`.
         * @return A position which line and character is the sum of the current line and
         * character and the corresponding deltas.
         */
        translate(lineDelta?: number, characterDelta?: number): Position;

        /**
         * Create a new position derived from this position.
         *
         * @param line Value that should be used as line value, default is the [existing value](#Position.line)
         * @param character Value that should be used as character value, default is the [existing value](#Position.character)
         * @return A position where line and character are replaced by the given values.
         */
        with(line?: number, character?: number): Position;
    }

    /**
     * A range represents an ordered pair of two positions.
     * It is guaranteed that [start](#Range.start).isBeforeOrEqual([end](#Range.end))
     *
     * Range objects are __immutable__. Use the [with](#Range.with),
     * [intersection](#Range.intersection), or [union](#Range.union) methods
     * to derive new ranges from an existing range.
     */
    declare export class Range {

        /**
         * The start position. It is before or equal to [end](#Range.end).
         * @readonly
         */
        start: Position;

        /**
         * The end position. It is after or equal to [start](#Range.start).
         * @readonly
         */
        end: Position;

        /**
         * Create a new range from two positions. If `start` is not
         * before or equal to `end`, the values will be swapped.
         *
         * @param start A position.
         * @param end A position.
         */
        constructor(start: Position, end: Position): void;

        /**
         * Create a new range from number coordinates. It is a shorter equivalent of
         * using `new Range(new Position(startLine, startCharacter), new Position(endLine, endCharacter))`
         *
         * @param startLine A zero-based line value.
         * @param startCharacter A zero-based character value.
         * @param endLine A zero-based line value.
         * @param endCharacter A zero-based character value.
         */
        constructor(startLine: number, startCharacter: number, endLine: number, endCharacter: number): void;

        /**
         * `true` iff `start` and `end` are equal.
         */
        isEmpty: boolean;

        /**
         * `true` iff `start.line` and `end.line` are equal.
         */
        isSingleLine: boolean;

        /**
         * Check if a position or a range is contained in this range.
         *
         * @param positionOrRange A position or a range.
         * @return `true` iff the position or range is inside or equal
         * to this range.
         */
        contains(positionOrRange: Position | Range): boolean;

        /**
         * Check if `other` equals this range.
         *
         * @param other A range.
         * @return `true` when start and end are [equal](#Position.isEqual) to
         * start and end of this range.
         */
        isEqual(other: Range): boolean;

        /**
         * Intersect `range` with this range and returns a new range or `undefined`
         * if the ranges have no overlap.
         *
         * @param range A range.
         * @return A range of the greater start and smaller end positions. Will
         * return undefined when there is no overlap.
         */
        intersection(range: Range): Range;

        /**
         * Compute the union of `other` with this range.
         *
         * @param other A range.
         * @return A range of smaller start position and the greater end position.
         */
        union(other: Range): Range;

        /**
         * Create a new range derived from this range.
         *
         * @param start A position that should be used as start. The default value is the [current start](#Range.start).
         * @param end A position that should be used as end. The default value is the [current end](#Range.end).
         * @return A range derived from this range with the given start and end position.
         * If start and end are not different this range will be returned.
         */
        with(start?: Position, end?: Position): Range;
    }

}

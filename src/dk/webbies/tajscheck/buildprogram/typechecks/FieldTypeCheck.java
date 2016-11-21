package dk.webbies.tajscheck.buildprogram.typechecks;

import dk.webbies.tajscheck.buildprogram.CheckType;
import dk.webbies.tajscheck.testcreator.test.check.Check;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 21-11-2016.
 */
public class FieldTypeCheck implements TypeCheck {
    private final String field;
    private final List<TypeCheck> fieldChecks;

    public FieldTypeCheck(String field, List<TypeCheck> fieldChecks) {
        this.field = field;
        this.fieldChecks = fieldChecks;
    }

    public String getField() {
        return field;
    }

    public List<TypeCheck> getFieldChecks() {
        return fieldChecks;
    }

    @Override
    public String getExpected() {
        return "field[" + field + "]:(" + CheckType.createIntersectionDescription(fieldChecks) + ")";
    }

    @Override
    public Check getCheck() {
        return Check.field(this.field, fieldChecks.stream().map(TypeCheck::getCheck).collect(Collectors.toList()));
    }
}

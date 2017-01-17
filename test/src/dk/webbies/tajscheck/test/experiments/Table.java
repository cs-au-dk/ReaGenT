package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 16-01-2017.
 */
public class Table {
    private final List<List<String>> table = Collections.synchronizedList(new ArrayList<>());
    private int size = -1;

    public void addRow(Collection<String> objects) {
        assertSize(objects.size());
        table.add(objects.stream().collect(Collectors.toList()));
    }

    private void assertSize(int newSize) {
        if (size == -1) {
            size = newSize;
        } else {
            assert size == newSize;
        }
    }

    public void setRow(int index, Collection<String> objects) {
        Util.ensureSize(table, index + 1);
        assertSize(objects.size());
        table.set(index, objects.stream().collect(Collectors.toList()));
    }

    private String print(String columnSeparator, String rowSeparator) {
        List<String> rows = table.stream().filter(Objects::nonNull).map(row -> String.join(columnSeparator, Util.replaceNulls(row, "-"))).collect(Collectors.toList());

        return String.join(rowSeparator, rows);
    }

    public String toCSV() {
        return print("\t", "\n");
    }

    public String toLatex() {
        return print(" & ", "\\\\ \\hline \n");
    }

    public Table transpose() {
        Table result = new Table();
        for (int i = 0; i < size; i++) {
            List<String> row = new ArrayList<>(table.size());
            for (List<String> list : table) {
                row.add(list.get(i));
            }
            result.addRow(row);
        }
        return result;
    }
}

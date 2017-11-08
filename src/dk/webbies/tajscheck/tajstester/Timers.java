package dk.webbies.tajscheck.tajstester;

import java.util.ArrayList;
import java.util.List;

public class Timers {

    private final boolean ENABLED = true;

    public enum Tags {
        PROPAGATING_TO_THIS_CONTEXT,
        PROPAGATING_BACK_TO_LOOP_ENTRY,
        INITIAL_STATE_PROPAGATION_TO_TEST_ENTRY,
        TEST_TRANSFER
    }

    private List<Long> starts = new ArrayList<>();

    private List<Long> totals = new ArrayList<>();

    public void start(Tags tag) {
        if(!ENABLED) return;
        if (tag.ordinal() >= starts.size())
            expand(starts, tag.ordinal());

        starts.set(tag.ordinal(), System.currentTimeMillis());
    }

    public void stop(Tags tag) {
        if(!ENABLED) return;
        if (tag.ordinal() < starts.size() && starts.get(tag.ordinal()) > 0) {
            long start = starts.get(tag.ordinal());
            long elapsed = System.currentTimeMillis() - start;
            if (tag.ordinal() >= totals.size()) {
                expand(totals, tag.ordinal());
            }
            Long old = totals.get(tag.ordinal());
            totals.set(tag.ordinal(), old + elapsed);
        }
    }

    private void expand(List<Long> l, int index) {
        while (l.size() < index + 1) {
            l.add(0L);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time spents\n");
        for (Tags t : Tags.values()) {
            if (t.ordinal() < totals.size() && totals.get(t.ordinal()) != null) {
                sb.append(t).append(": ").append(totals.get(t.ordinal()) / (double) 1000).append("s");
            } else {
                sb.append(t).append(": ").append(0);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

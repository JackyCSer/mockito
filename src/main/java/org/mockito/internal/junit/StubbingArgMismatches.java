package org.mockito.internal.junit;

import org.mockito.internal.util.MockitoLogger;
import org.mockito.invocation.Invocation;

import java.util.*;

/**
 * Contains stubbing arg mismatches, knows how to format them
 */
class StubbingArgMismatches {

    final Map<Invocation, Set<Invocation>> mismatches = new LinkedHashMap<Invocation, Set<Invocation>>();

    public void add(Invocation invocation, Invocation stubbing) {
        Set<Invocation> matchingInvocations = mismatches.get(stubbing);
        if (matchingInvocations == null) {
            matchingInvocations = new LinkedHashSet<Invocation>();
            mismatches.put(stubbing, matchingInvocations);
        }
        matchingInvocations.add(invocation);
    }

    public void format(String testName, MockitoLogger logger) {
        if (mismatches.isEmpty()) {
            return;
        }

        StubbingHint hint = new StubbingHint(testName);
        //TODO 384 it would be nice to make the String look good if x goes multiple digits (padding)
        int x = 1;
        for (Map.Entry<Invocation, Set<Invocation>> m : mismatches.entrySet()) {
            hint.appendLine(x++, ". Unused... ", m.getKey().getLocation());
            for (Invocation invocation : m.getValue()) {
                hint.appendLine(" ...args ok? ", invocation.getLocation());
            }
        }

        logger.log(hint.toString());
    }

    public int size() {
        return mismatches.size();
    }

    public String toString() {
        return "" + mismatches;
    }
}

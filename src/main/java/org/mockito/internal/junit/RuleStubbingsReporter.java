package org.mockito.internal.junit;

import org.mockito.internal.util.MockitoLogger;
import org.mockito.invocation.Invocation;
import org.mockito.listeners.StubbingListener;

import java.util.*;

class RuleStubbingsReporter implements StubbingListener {

    private final Set<Invocation> unstubbedInvocations = new LinkedHashSet<Invocation>();
    private final Set<Invocation> stubbings = new LinkedHashSet<Invocation>();

    public void newStubbing(Invocation stubbing) {
        stubbings.add(stubbing);

        //Removing 'fake' unstubbed invocations
        //'stubbingNotFound' event (that populates unstubbed invocations) is also triggered
        // during regular stubbing using when(). It's a quirk of when() syntax. See javadoc for stubbingNotFound().
        unstubbedInvocations.remove(stubbing);
    }

    public void usedStubbing(Invocation stubbing, Invocation actual) {
        stubbings.remove(stubbing);
    }

    public void stubbingNotFound(Invocation actual) {
        unstubbedInvocations.add(actual);
    }

    void printStubbingMismatches(MockitoLogger logger) {
        StubbingArgMismatches mismatches = new StubbingArgMismatches();
        for (Invocation i : unstubbedInvocations) {
            for (Invocation stubbing : stubbings) {
                //method name & mock matches
                //TODO 384 tighten coverage
                if (stubbing.getMock() == i.getMock()
                        && stubbing.getMethod().getName().equals(i.getMethod().getName())) {
                    mismatches.add(i, stubbing);
                }
            }
        }
        mismatches.log(logger);
    }

    void printUnusedStubbings(MockitoLogger logger) {
        if (stubbings.isEmpty()) {
            return;
        }

        StringBuilder hint = new StringBuilder("[MockitoHint] See javadoc for MockitoHint class.");
        for (Invocation unused : stubbings) {
            hint.append("\n[MockitoHint] unused ").append(unused.getLocation());
        }
        logger.log(hint.toString());
    }
}

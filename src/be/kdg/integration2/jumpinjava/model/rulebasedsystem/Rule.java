package be.kdg.integration2.jumpinjava.model.rulebasedsystem;

import java.util.List;
import java.util.function.Predicate;

public class Rule {
    private final String name;
    private final Predicate<List<Fact>> condition;
    private final Action action;

    public Rule(String name, Predicate<List<Fact>> condition, Action action) {
        this.name = name;
        this.condition = condition;
        this.action = action;
    }

    public boolean evaluate(List<Fact> facts) {
        return condition.test(facts);
    }

    public Action getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    // Nested interface to define actions
    public interface Action {
        void execute();
    }
}
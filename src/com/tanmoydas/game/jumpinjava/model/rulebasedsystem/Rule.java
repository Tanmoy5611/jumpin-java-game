package com.tanmoydas.game.jumpinjava.model.rulebasedsystem;

import java.util.List;
import java.util.function.Predicate;

/* Represents a single rule in the rule-based AI system.
   A rule consists of a condition and an action.
 */
public class Rule {

    private final RuleName name;
    private final Predicate<List<Fact>> condition;
    private final Action action;

    public Rule(RuleName name,
                Predicate<List<Fact>> condition,
                Action action) {
        this.name = name;
        this.condition = condition;
        this.action = action;
    }

    public boolean evaluate(List<Fact> facts) {
        return condition.test(facts);
    }

    public void execute(ActionContext context) {
        action.execute(context);
    }

    public RuleName getName() {
        return name;
    }

   // Supporting contracts

    // Functional interfaces
    @FunctionalInterface
    public interface Action {
        void execute(ActionContext context);
    }
}
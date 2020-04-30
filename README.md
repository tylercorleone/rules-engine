Rules Engine
============

A rules engine tests some **facts** on a given set of **rules** in order to obtain the **result** of the rules that gets satisfied by those facts and make them execute some **actions**.

Examples:
 1. to manage user's permissions, we could create a set of rules, each of which checking for a specific condition (that is a predicate on some facts e.g. *"has the user been registered for more than two years?"*) and containing the permissions that would be applicable if the condition gets verified
 2. to activate different business flows based on specific conditions, each rule will verify its own condition on the given facts, and have an action that triggers the corresponding flow or implements it

## Key concepts:

### Rule:

An object that represents a business rule. Is composed by the following fields:
 - **name**: a unique rule name within a rules set
 - **condition**: a predicate that, given some **facts**, must be verified for the rule to match
 - **result**: a valid information in a context of facts in which the rule matches
 - **action**: the rule's action that will be triggered if the rule matches
 - **priority**: the priority of the rule over the others
 - **description**: a brief description of the rule

### Fact:

Can be everything represents an information in a given context.  
In the above example, *"has the user been registered for more than two years?"*, the fact is the user's registration period (*one day*, *three years* etc...) and the condition wants to verify that it is greater than two years.

### Rules engine:
 
Takes a set of **rules** and **facts** and **iterates** over the rules starting from the one with the highest **priority** and tests each rule's condition with the given facts.

This rules engine offers four methods to process rules and facts. Two stop at the first match, two continue, two return the result of the matching rules, two return nothing.

**Type parameters:**  
 T - the type of the facts  
 V - the type of the result of the rules  

**Method summary:**
 - **void fire(Rules<T, V> rules, T facts)**: triggers the action of the first matching rule.
 - **void fireAll(Rules<T, V> rules, T facts)**: triggers the action of all the matching rules.
 - **Optional<V> fireAndGet(Rules<T, V> rules, T facts)**: triggers the action of the first matching rule and returns its result, or an empty optional if no rule have been matched.
 - **Results<V> fireAndGetAll(Rules<T, V> rules, T facts)**: triggers the action of all the matching rules and returns their results. The results are accessible by rule name or by stream.

//package it.sky.rulesengine.examples.advertising;
//
//import java.util.Collection;
//import java.util.List;
//
//import it.sky.rulesengine.api.RulesSet;
//import it.sky.rulesengine.core.api.Rule;
//import it.sky.rulesengine.core.api.RulesEngine;
//import it.sky.rulesengine.core.ImmutableRulesSet;
//import it.sky.rulesengine.core.impl.DefaultRulesEngine;
//import it.sky.rulesengine.examples.advertising.rulesengine.EmailRulesFactory;
//import it.sky.rulesengine.util.VoidResult;
//
///**
// * A company's advertisement policy establish that user will receive daily
// * e-mail, except on Sunday. But they will receive a special e-mail on their
// * birthday, even on Sunday.
// */
//public class AdvertisementEngine {
//
//    private static final RulesEngine rulesEngine = DefaultRulesEngine.create();
//
//    private final Collection<Rule<User, Void>> emailRules;
//
//    public AdvertisementEngine(EmailRulesFactory rulesFactory) {
//        this.emailRules = ImmutableRulesSet.of(rulesFactory.buildBirthdayRule(), rulesFactory.buildSundayRule(),
//                rulesFactory.buildDefaultRule());
//    }
//
//    public void advertiseUsers(List<User> users) {
//        users.forEach(u -> rulesEngine.fire(emailRules, u));
//    }
//
//}

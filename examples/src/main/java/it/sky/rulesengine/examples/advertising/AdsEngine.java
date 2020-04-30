package it.sky.rulesengine.examples.advertising;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.factory.api.RulesFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public abstract class AdsEngine {

    private final RulesFactory<AdFacts, Void> rulesFactory;
    private final RulesEngine rulesEngine = DefaultRulesEngine.create();

    protected abstract void sendEmail(Email email);

    public void run(List<AdFacts> adFacts) {
        Collection<Rule<AdFacts, Void>> adRules = rulesFactory.get();
        adFacts.forEach(f -> rulesEngine.applyFirst(adRules, f));
    }

    public void sendDailyEmail(AdFacts adFacts) {
        User user = adFacts.getUser();
        Email email = new Email(user.getEmailAddress(), String.format("Goodmorning %s!", user.getName()));
        sendEmail(email);
    }

    public void sendBirthdayEmail(AdFacts adFacts) {
        User user = adFacts.getUser();
        Email email = new Email(user.getEmailAddress(), String.format("Happy birthday %s!", user.getName()));
        sendEmail(email);
    }

    public void sendPromoMail(AdFacts adFacts) {
        Email email = new Email(adFacts.getUser().getEmailAddress(),
                String.format("Promo day at %s!", adFacts.getCompany().getName()));
        sendEmail(email);
    }

    @Data
    public static class Email {
        private final String to;
        private final String subject;
    }

}

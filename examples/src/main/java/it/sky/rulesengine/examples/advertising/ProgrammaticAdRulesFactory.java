package it.sky.rulesengine.examples.advertising;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.factory.api.RulesFactory;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Creates a set of advertisement rules using the Java API.
 */
@RequiredArgsConstructor
public class ProgrammaticAdRulesFactory implements RulesFactory<AdFacts, Void> {

    private static final int FIRST = Integer.MAX_VALUE;
    private static final int SECOND = Integer.MAX_VALUE - 1;
    private static final int THIRD = Integer.MAX_VALUE - 2;
    private static final int LAST = Integer.MIN_VALUE;
    private static final Consumer<AdFacts> doNothing = u -> {
    };

    private final AdsEngine emailService;
    private final Clock clock;

    @Override
    public Collection<Rule<AdFacts, Void>> get() {
        return Arrays.asList(birthdayRule(), sundayRule(), promoDayRule(), defaultRule());
    }

    private Rule<AdFacts, Void> birthdayRule() {
        return CompositeRule.create("birthdayRule", this::isUserBirthday, f -> emailService.sendBirthdayEmail())
                .withPriority(FIRST);
    }

    private Rule<AdFacts, Void> sundayRule() {
        return CompositeRule.create("sundayRule", this::isSunday, doNothing).withPriority(SECOND);
    }

    private Rule<AdFacts, Void> promoDayRule() {
        return CompositeRule.create("blackFridayRule", this::isPromoDay, emailService::sendPromoMail)
                .withPriority(THIRD);
    }

    private Rule<AdFacts, Void> defaultRule() {
        return CompositeRule.create("defaultRule", u -> true, emailService::sendDailyEmail).withPriority(LAST);
    }

    private boolean isUserBirthday(AdFacts adFacts) {
        User user = adFacts.getUser();
        return localDate().withYear(user.getBirthDate().getYear()).equals(user.getBirthDate());
    }

    private boolean isSunday(AdFacts adFacts) {
        return localDate().getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    private boolean isPromoDay(AdFacts adFacts) {
        Company company = adFacts.getCompany();
        return localDate().withYear(company.getFoundationDate().getYear()).equals(company.getFoundationDate());
    }

    private LocalDate localDate() {
        return clock.instant().atOffset(ZoneOffset.UTC).toLocalDate();
    }

}

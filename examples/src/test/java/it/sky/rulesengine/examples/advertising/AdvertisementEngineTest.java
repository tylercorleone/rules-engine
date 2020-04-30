package it.sky.rulesengine.examples.advertising;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.factory.api.RulesFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * A company's advertisement policy establish that an user:
 * - could receive a daily e-mail
 * - could receive a promo e-mail
 * - will not receive daily and promo e-mails on Sunday
 * - will receive congratulations e-mail on their birthday, even on Sunday
 */
public abstract class AdvertisementEngineTest {

    abstract Collection<Rule<AdFacts, Void>> buildRules(AdsEngine adEngine, LocalDate localDate);

    static final String PAOLO = "Paolo";
    static final String VALENTINA = "Valentina";
    static final LocalDate SUN_1981_09_13 = LocalDate.parse("1981-09-13");
    static final LocalDate SAT_1985_12_07 = LocalDate.parse("1985-12-07");
    static final LocalDate MON_1996_06_16 = LocalDate.parse("1996-06-16");
    static final LocalDate SAT_2019_06_15 = LocalDate.parse("2019-06-15");
    static final LocalDate SUN_2019_06_16 = LocalDate.parse("2019-06-16");
    static final LocalDate FRI_2019_09_13 = LocalDate.parse("2019-09-13");
    static final LocalDate TUE_2020_06_16 = LocalDate.parse("2020-06-16");
    static final LocalDate SUN_2025_12_07 = LocalDate.parse("2025-12-07");

    @Fixture
    String paoloEmail;
    @Fixture
    String valentinaEmail;
    @Fixture
    String companyName;

    DummyAdvertisementEngine adEngine;

    Company company;

    List<User> users;

    void simulateAdvertising(LocalDate localDate) {
        adEngine = new DummyAdvertisementEngine(() -> buildRules(adEngine, localDate));
        List<AdFacts> adFacts = users.stream().map(u -> new AdFacts(u, company)).collect(Collectors.toList());
        adEngine.run(adFacts);
    }

    @BeforeEach
    void beforeEach() {
        FixtureAnnotations.initFixtures(this);
        MockitoAnnotations.initMocks(this);
        company = new Company(companyName, MON_1996_06_16);
        users = Arrays.asList(new User(PAOLO, paoloEmail, SAT_1985_12_07),
                new User(VALENTINA, valentinaEmail, SUN_1981_09_13));
    }

    /**
     * On Saturday 2019-06-15 Paolo and Valentina will receive an ordinary daily
     * e-mail, because it's not their birthday and it's not Sunday.
     */
    @Test
    void shouldSendDailyMail() {
        simulateAdvertising(SAT_2019_06_15);

        assertEquals("Goodmorning " + PAOLO + "!", adEngine.getLastEmailSentTo(paoloEmail).getSubject());
        assertEquals("Goodmorning " + VALENTINA + "!", adEngine.getLastEmailSentTo(valentinaEmail).getSubject());
    }

    /**
     * It's promo day and it's not Sunday or user's birthday.
     */
    @Test
    void shouldSendPromoMail() {
        simulateAdvertising(TUE_2020_06_16);

        assertEquals("Promo day at " + companyName + "!", adEngine.getLastEmailSentTo(paoloEmail).getSubject());
        assertEquals("Promo day at " + companyName + "!", adEngine.getLastEmailSentTo(valentinaEmail).getSubject());
    }

    /**
     * It's promo day but Paolo and Valentina will
     * not receive any e-mail. It's Sunday!
     */
    @Test
    void shouldNotSendAnyEmailsOnSunday() {
        simulateAdvertising(SUN_2019_06_16);

        assertNull(adEngine.getLastEmailSentTo(paoloEmail));
        assertNull(adEngine.getLastEmailSentTo(valentinaEmail));
    }

    /**
     * On Friday 2019-09-13 Paolo will receive an ordinary daily e-mail, but
     * Valentina will receive her birthday mail!
     */
    @Test
    void shouldSendBirthdayEmails() {
        simulateAdvertising(FRI_2019_09_13);

        assertEquals("Goodmorning " + PAOLO + "!", adEngine.getLastEmailSentTo(paoloEmail).getSubject());
        assertEquals("Happy birthday " + VALENTINA + "!", adEngine.getLastEmailSentTo(valentinaEmail).getSubject());
    }

    /**
     * On Sunday 2025-12-07 Valentina will not receive any e-mail, but Paolo will
     * receive his birthday mail!
     */
    @Test
    void shouldSendBirthdayEmailsEvenOnSaturday() {
        simulateAdvertising(SUN_2025_12_07);

        assertEquals("Happy birthday " + PAOLO + "!", adEngine.getLastEmailSentTo(paoloEmail).getSubject());
        assertNull(adEngine.getLastEmailSentTo(valentinaEmail));
    }

    public static class DummyAdvertisementEngine extends AdsEngine {

        public DummyAdvertisementEngine(RulesFactory<AdFacts, Void> rulesFactory) {
            super(rulesFactory);
        }

        private final Map<String, Email> lastEmailsSent = new HashMap<>();

        @Override
        public void sendEmail(Email email) {
            lastEmailsSent.put(email.getTo(), email);
        }

        public Email getLastEmailSentTo(String to) {
            return lastEmailsSent.get(to);
        }

    }

}

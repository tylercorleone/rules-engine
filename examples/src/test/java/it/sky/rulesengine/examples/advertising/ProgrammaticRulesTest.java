package it.sky.rulesengine.examples.advertising;

import it.sky.rulesengine.core.api.Rule;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;

/**
 *
 */
public class ProgrammaticRulesTest extends AdvertisementEngineTest {

    @Override
    Collection<Rule<AdFacts, Void>> buildRules(AdsEngine adEngine, LocalDate localDate) {
        ProgrammaticAdRulesFactory rulesFactory = new ProgrammaticAdRulesFactory(adEngine,
                Clock.fixed(localDate.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

        return rulesFactory.get();
    }

}

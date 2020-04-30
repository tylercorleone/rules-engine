package it.sky.rulesengine.examples.lifepriorities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ActionManagerTest {

    private static ActivityManager activityManager;

    @BeforeAll
    static void load() {
        activityManager = new ActivityManager();
    }

    @Test
    void testWork() {
        FactsMap factsMap = new FactsMap();
        factsMap.put("energy", 1);
        factsMap.put("fat", 0.1);

        Optional<Activity> result = activityManager.evaluate(factsMap);

        assertEquals(Action.WORK, result.get().getAction());
        assertEquals(-200, result.get().getEnergyLoss());
        assertEquals(true, result.get().getMakesYouEarnMoney());
    }

}

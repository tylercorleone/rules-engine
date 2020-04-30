package it.sky.rulesengine.examples.lifepriorities;

import lombok.Data;

@Data
class Activity {
    private final Action action;
    private final int energyLoss;
    private final boolean makesYouEarnMoney;
}


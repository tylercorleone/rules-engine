package it.sky.rulesengine.examples.advertising;

import lombok.Data;
import lombok.NonNull;
import org.apache.commons.jexl3.MapContext;

@Data
public class AdFacts extends MapContext {

    private final User user;
    private final Company company;

    public AdFacts(@NonNull final User user, @NonNull final Company company) {
        this.user = user;
        this.company = company;
        set("user", user);
        set("company", company);
        set("adFacts", this);
    }

}

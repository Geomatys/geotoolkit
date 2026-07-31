package org.geotoolkit.stac.process;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StacQueryFilterTest {

    @Test
    public void nullOrEmptyQueryAlwaysMatch() {
        assertTrue(StacQueryFilter.matches(Map.of("eo:cloud_cover", 90), null));
        assertTrue(StacQueryFilter.matches(Map.of("eo:cloud_cover", 90), Map.of()));
    }

    @Test
    public void lteOperatorFiltersOnPropertyValue() {
        Map<String, Map<String, Object>> query = Map.of("eo:cloud_cover", Map.of("lte", 50));

        assertTrue(StacQueryFilter.matches(Map.of("eo:cloud_cover", 20), query));
        assertFalse(StacQueryFilter.matches(Map.of("eo:cloud_cover", 90), query));
    }

    @Test
    public void missingItemPropertyDoesNotMatchNumericOperator() {
        Map<String, Map<String, Object>> query = Map.of("eo:cloud_cover", Map.of("lte", 50));

        assertFalse(StacQueryFilter.matches(Map.of(), query));
    }

    @Test
    public void eqOperatorMatchesStringValue() {
        Map<String, Map<String, Object>> query = Map.of("platform", Map.of("eq", "sentinel-2b"));

        assertTrue(StacQueryFilter.matches(Map.of("platform", "sentinel-2b"), query));
        assertFalse(StacQueryFilter.matches(Map.of("platform", "sentinel-2a"), query));
    }

    @Test
    public void multiplePropertiesAreAndCombined() {
        Map<String, Map<String, Object>> query = Map.of(
                "eo:cloud_cover", Map.of("lte", 50),
                "platform", Map.of("eq", "sentinel-2b"));

        assertTrue(StacQueryFilter.matches(Map.of("eo:cloud_cover", 10, "platform", "sentinel-2b"), query));
        assertFalse(StacQueryFilter.matches(Map.of("eo:cloud_cover", 90, "platform", "sentinel-2b"), query));
    }
}
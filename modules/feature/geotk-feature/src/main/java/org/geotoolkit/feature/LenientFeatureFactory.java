package org.geotoolkit.feature;

public class LenientFeatureFactory extends AbstractFeatureFactory {
    public LenientFeatureFactory() {
        validating = false;
    }
}

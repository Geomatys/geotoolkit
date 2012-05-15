package org.geotoolkit.feature;

import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.FeatureTypeFactory;

/**
 *
 * @author Alexis MANIN
 */
public class LenientFeatureFactoryTest extends AbstractComplexFeatureFactoryTest{

    private static final LenientFeatureFactory FF = new LenientFeatureFactory();
    protected static final DefaultFeatureTypeFactory FTF = new DefaultFeatureTypeFactory();
    
    public LenientFeatureFactoryTest() {
        super(false);
    }   
    
    
    @Override
    public FeatureFactory getFeatureFactory() {
        return FF;
    }

    @Override
    public FeatureTypeFactory getFeatureTypeFactory() {
        return FTF;
    }
    
    
    
}

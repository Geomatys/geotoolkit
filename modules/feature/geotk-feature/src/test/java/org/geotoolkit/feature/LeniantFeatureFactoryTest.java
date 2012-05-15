/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.feature;

import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.FeatureTypeFactory;

/**
 *
 * @author geoadmin
 */
public class LeniantFeatureFactoryTest extends AbstractComplexFeatureFactoryTest{

    private static final LenientFeatureFactory FF = new LenientFeatureFactory();
    protected static final DefaultFeatureTypeFactory FTF = new DefaultFeatureTypeFactory();
    
    public LeniantFeatureFactoryTest() {
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

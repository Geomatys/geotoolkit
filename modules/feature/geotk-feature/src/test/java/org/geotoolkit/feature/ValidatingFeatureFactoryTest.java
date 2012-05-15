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
 * @author Alexis Manin
 */
public class ValidatingFeatureFactoryTest extends AbstractComplexFeatureFactoryTest {
    
    private static final ValidatingFeatureFactory FF = new ValidatingFeatureFactory();
    protected static final DefaultFeatureTypeFactory FTF = new DefaultFeatureTypeFactory();
    
    public ValidatingFeatureFactoryTest() {
        super(true);
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

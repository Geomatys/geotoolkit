/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.feature.type;

import org.opengis.feature.type.FeatureTypeFactory;

/**
 *
 * @author geoadmin
 */
public class DefaultFeatureTypeFactoryTest extends AbstractComplexFeatureTypeFactoryTest {

    protected static final DefaultFeatureTypeFactory FTF = new DefaultFeatureTypeFactory();
    
    public DefaultFeatureTypeFactoryTest(){
        
    }
    
    @Override
    public FeatureTypeFactory getFeatureTypeFactory() {
        return FTF;
    }
    
}

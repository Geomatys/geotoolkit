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
public class DefaultFeatureTypeTest extends AbstractComplexFeatureTypeTest {

    protected static final DefaultFeatureTypeFactory FTF = new DefaultFeatureTypeFactory();
    
    public DefaultFeatureTypeTest(){
        
    }
    
    @Override
    public FeatureTypeFactory getFeatureTypeFactory() {
        return FTF;
    }
    
}

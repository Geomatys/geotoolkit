/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.feature;

import java.util.Map;
import org.geotoolkit.referencing.crs.AbstractCRS;
import org.apache.sis.util.ComparisonMode;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author geoadmin
 */
public class MockCRS extends AbstractCRS{
    
    public MockCRS(Map<String, ?> map){

        super(map, new MockCS());
    }
    
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if(object instanceof CoordinateReferenceSystem){
            CoordinateReferenceSystem tmp = (CoordinateReferenceSystem) object;
            if(this.nameMatches(tmp.getName().toString()))
                return true;
        }
        return false;
    }
}

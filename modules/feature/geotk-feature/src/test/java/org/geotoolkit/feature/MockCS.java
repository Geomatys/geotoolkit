/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.feature;

import java.util.Collection;
import java.util.Set;
import org.geotoolkit.feature.type.Name;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;


/**
 *
 * @author geoadmin
 */
public class MockCS implements CoordinateSystem {
    
    public MockCS(){
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public CoordinateSystemAxis getAxis(int dimension) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Identifier getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<GenericName> getAlias() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Identifier> getIdentifiers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InternationalString getRemarks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toWKT() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}

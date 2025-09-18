/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.dggs.mgrs;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.referencing.gazetteer.MilitaryGridReferenceSystem;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class MgrsCoder extends DiscreteGlobalGridReferenceSystem.Coder{

    private final MgrsDggrs dggrs;
    private final CoordinateReferenceSystem baseCrs;
    private final MilitaryGridReferenceSystem.Coder coder;

    public MgrsCoder(MgrsDggrs dggrs) {
        this.dggrs = dggrs;
        this.baseCrs = this.dggrs.getGridSystem().getCrs();
        this.coder = MgrsDggrs.MGRS.createCoder();
    }

    @Override
    public MgrsDggrs getReferenceSystem() {
        return dggrs;
    }

    @Override
    public Quantity<?> getPrecision(DirectPosition dp) {
        return coder.getPrecision(dp);
    }

    @Override
    public void setPrecision(Quantity<?> qnt, DirectPosition dp) throws IncommensurableException {
        coder.setPrecision(qnt, dp);
    }

    @Override
    public int getPrecisionLevel() {
        int precision = (int) coder.getPrecision();
        switch (precision) {
            case 1 : return 5;
            case 10 : return 4;
            case 100 : return 3;
            case 1000 : return 2;
            case 10000 : return 1;
            case 100000 : return 0;
            //case 1000000 : return 0; //todo wainting for SIS fix
            default: throw new IllegalArgumentException("Precision " + precision + " could not be mapped to a level");
        }
    }

    @Override
    public void setPrecisionLevel(int level) throws IncommensurableException {
        switch (level) {
            //case 0 : coder.setPrecision(1000000);  //todo wainting for SIS fix
            case 0 : coder.setPrecision(100000);
            case 1 : coder.setPrecision(10000);
            case 2 : coder.setPrecision(1000);
            case 3 : coder.setPrecision(100);
            case 4 : coder.setPrecision(10);
            case 5 : coder.setPrecision(1);
            default: throw new IncommensurableException("Requested level if greater then maximum level");
        }
    }

    @Override
    public String encode(DirectPosition dp) throws TransformException {
        return coder.encode(dp);
    }

    @Override
    public Object encodeIdentifier(DirectPosition dp) throws TransformException {
        return encode(dp);
    }

}

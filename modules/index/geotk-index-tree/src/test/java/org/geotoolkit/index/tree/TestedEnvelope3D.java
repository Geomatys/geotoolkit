/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Class used only to {@link ReaderWriterTest}.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class TestedEnvelope3D extends GeneralEnvelope implements Externalizable {

    public TestedEnvelope3D(CoordinateReferenceSystem crs) {
        super(crs);
    }

    public TestedEnvelope3D() {
        super(DefaultEngineeringCRS.CARTESIAN_3D);
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        int dim = super.getDimension();
        for(int i = 0; i<dim; i++){
            out.writeDouble(super.getLower(i));
        }
        for(int i = 0; i<dim; i++){
            out.writeDouble(super.getUpper(i));
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        double[] envelop = new double[6];
        for (int i=0; i<6; i++) {
            envelop[i] = in.readDouble();
        }
        setEnvelope(envelop);
    }
}

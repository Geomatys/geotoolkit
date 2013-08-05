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
package org.geotoolkit.index.tree.io;

import org.geotoolkit.index.tree.mapper.TreeElementMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TreeElementMapper implementation adapted for tests with Node storage in memory.
 *
 * @author Remi Marechal (Geomatys).
 */
public class TreeElementMapperTest implements TreeElementMapper<double[]> {

    private final CoordinateReferenceSystem crs;
    private final List<double[]> lData;
    private final List<Integer> lID;

    public TreeElementMapperTest(CoordinateReferenceSystem crs) {
        this.crs   = crs;
        this.lData = new ArrayList<double[]>();
        this.lID   = new ArrayList<Integer>();
    }
    
    @Override
    public int getTreeIdentifier(double[] object) {
        for (int i = 0, s = lData.size(); i < s; i++) {
            if (Arrays.equals(lData.get(i), object)) {
                return lID.get(i);
            }
        }
        throw new IllegalStateException("impossible to found treeIdentifier.");
    }

    @Override
    public Envelope getEnvelope(double[] object) {
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setEnvelope(object);
        return env;
    }

    @Override
    public void setTreeIdentifier(double[] object, int treeIdentifier) {
        lData.add(object);
        lID.add(treeIdentifier);
    }

    @Override
    public double[] getObjectFromTreeIdentifier(int treeIdentifier) {
        for (int i = 0, l = lID.size(); i < l; i++) {
            if (lID.get(i) == treeIdentifier) {
                return lData.get(i);
            }
        }
        throw new IllegalStateException("impossible to found Data.");
    }

    @Override
    public void clear() {
        lData.clear();
        lID.clear();
    }
}

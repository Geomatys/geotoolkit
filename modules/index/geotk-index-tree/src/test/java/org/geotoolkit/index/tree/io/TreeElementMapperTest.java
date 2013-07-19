/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author rmarechal
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

    public void clear() {
        lData.clear();
        lID.clear();
    }
}

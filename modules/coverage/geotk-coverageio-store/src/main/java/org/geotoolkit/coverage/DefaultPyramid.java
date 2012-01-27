/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.util.converter.Classes;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default pyramid
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPyramid implements Pyramid{

    private final String id = UUID.randomUUID().toString();
    private final PyramidSet set;
    private final CoordinateReferenceSystem crs;
    private final SortedMap<Double,GridMosaic> mosaics = new TreeMap<Double, GridMosaic>(            
        new Comparator<Double>(){
            @Override
            public int compare(Double o1, Double o2) {
                //reverse order
                return -o1.compareTo(o2);
            }
    });

    public DefaultPyramid(PyramidSet set, CoordinateReferenceSystem crs) {
        this.set = set;
        this.crs = crs;
    }

    @Override
    public String getId() {
        return id;
    }
    
    public SortedMap<Double, GridMosaic> getMosaics() {
        return mosaics;
    }
    
    @Override
    public PyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public double[] getScales() {
        final double[] scales = new double[mosaics.size()];
        
        int i=0;
        for(Entry<Double,?> entry : mosaics.entrySet()){
            scales[i] = entry.getKey();
            i++;
        }
        
        return scales;
    }

    @Override
    public GridMosaic getMosaic(int index) {
        int i=0;
        for(Entry<Double,GridMosaic> entry : mosaics.entrySet()){
            if(i == index){
                return entry.getValue();
            }
            i++;
        }
        return null;
    }
    
    @Override
    public String toString(){
        return Trees.toString(
                Classes.getShortClassName(this)
                +" "+IdentifiedObjects.getIdentifier(getCoordinateReferenceSystem()) 
                +" "+getId(), 
                getMosaics().values());
    }
    
}

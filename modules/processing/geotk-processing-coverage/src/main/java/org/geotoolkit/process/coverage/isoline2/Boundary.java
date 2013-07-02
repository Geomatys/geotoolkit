/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.process.coverage.isoline2;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Boundary {
    
    /**
     * Geometries in construction
     */
    public LinkedList<Coordinate> HLeft, HMiddle, HRight;
    public LinkedList<Coordinate> VTop, VMiddle, VBottom;
    
    public List<LinkedList<Coordinate>> getConstructions(){
        final List<LinkedList<Coordinate>> contructions = new ArrayList<LinkedList<Coordinate>>();
        if(HLeft!=null) contructions.add(HLeft);
        if(HMiddle!=null) contructions.add(HMiddle);
        if(HRight!=null) contructions.add(HRight);
        if(VTop!=null) contructions.add(VTop);
        if(VMiddle!=null) contructions.add(VMiddle);
        if(VBottom!=null) contructions.add(VBottom);
        return contructions;
    }
    
}

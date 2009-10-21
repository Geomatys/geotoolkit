/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display3d.controller;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.display3d.container.A3DContainer;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class LocationSensitiveUpdater extends Thread{

    private final Map<Double,Object[]> sensitives;
    private final A3DContainer container;

    private final Vector3 lastestPosition;
    private final Vector3 currentPosition;

    public LocationSensitiveUpdater(A3DContainer container) {
        sensitives = Collections.synchronizedSortedMap(new TreeMap<Double, Object[]>());
        lastestPosition = new Vector3();
        currentPosition = new Vector3();
        this.container = container;
    }

    public void put(LocationSensitiveGraphic graphic, double tolerance){
        Object[] combination = sensitives.get(tolerance);
        if(combination == null){
            List<LocationSensitiveGraphic> graphics = new ArrayList<LocationSensitiveGraphic>();
            graphics.add(graphic);
            combination = new Object[2];
            combination[0] = new Vector3();
            combination[1] = graphics;
            sensitives.put(tolerance, combination);
        }else{
            ((List<LocationSensitiveGraphic>)combination[1]).add(graphic);
        }
    }

    void updateCameraLocation(ReadOnlyVector3 position){
        synchronized(lastestPosition){
            this.lastestPosition.set(position);
        }
    }

    @Override
    public void run() {
        while(true){

            synchronized(lastestPosition){
                currentPosition.set(lastestPosition);
            }

            //obtain the coordiante in geographic crs
            final ReadOnlyVector3 corrected = container.correctLocation(currentPosition);

            for(Double key : sensitives.keySet()){
                final Object[] combination = sensitives.get(key);
                final Vector3 vect = (Vector3) combination[0];
                final List<LocationSensitiveGraphic> graphics = (List<LocationSensitiveGraphic>) combination[1];

                if(vect.distance(currentPosition) > key){
                    vect.set(currentPosition);
                    for(LocationSensitiveGraphic gra : graphics){
                        gra.update(corrected);
                    }
                }
            }
            
            try {
                //we dont need to consume much cpu
                sleep(100);
            } catch (InterruptedException ex) {
                Logging.getLogger(LocationSensitiveUpdater.class).log(Level.SEVERE, null, ex);
            }

        }


    }

}

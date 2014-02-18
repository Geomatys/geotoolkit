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

import java.util.Collection;

/**
 * Define a triangle as a line routing boundary.
 * 
 *     + ④
 *    /|
 *   + + ⑤
 *  /  |
 * +-+-+ ⑥
 *① ② ③
 * 
 * ① HLeft
 * ② HMiddle
 * ③ HRight
 * ④ VTop
 * ⑤ VMiddle
 * ⑥ VBottom
 * 
 * @author Johann Sorel (Geomatys)
 */
public class Boundary {
    
    /**
     * Geometries in construction
     */
    public Construction.Edge HLeft, HMiddle, HRight;
    public Construction.Edge VTop, VMiddle, VBottom;
    
    public void getConstructions(Collection<Construction> contructions){
        if(HLeft!=null) contructions.add(HLeft.getConstruction());
        if(HMiddle!=null) contructions.add(HMiddle.getConstruction());
        if(HRight!=null) contructions.add(HRight.getConstruction());
        if(VTop!=null) contructions.add(VTop.getConstruction());
        if(VMiddle!=null) contructions.add(VMiddle.getConstruction());
        if(VBottom!=null) contructions.add(VBottom.getConstruction());
    }
    
    public void checkIncoherence(){
        if(HLeft!=null){
            if(HLeft==HMiddle || HLeft==HRight || HLeft==VTop || HLeft==VMiddle || HLeft==VBottom){
                throw new RuntimeException("Edge appears more then once");
            }
        }
        if(HMiddle!=null){
            if(HMiddle==HLeft || HMiddle==HRight || HMiddle==VTop || HMiddle==VMiddle || HMiddle==VBottom){
                throw new RuntimeException("Edge appears more then once");
            }
        }
        if(HRight!=null){
            if(HRight==HLeft || HRight==HMiddle || HRight==VTop || HRight==VMiddle || HRight==VBottom){
                throw new RuntimeException("Edge appears more then once");
            }
        }
        if(VTop!=null){
            if(VTop==HLeft || VTop==HMiddle || VTop==HRight || VTop==VMiddle || VTop==VBottom){
                throw new RuntimeException("Edge appears more then once");
            }
        }
        if(VMiddle!=null){
            if(VMiddle==HLeft || VMiddle==HMiddle || VMiddle==HRight || VMiddle==VTop || VMiddle==VBottom){
                throw new RuntimeException("Edge appears more then once");
            }
        }
        if(VBottom!=null){
            if(VBottom==HLeft || VBottom==HMiddle || VBottom==HRight || VBottom==VTop || VBottom==VMiddle){
                throw new RuntimeException("Edge appears more then once");
            }
        }
    }
    
}

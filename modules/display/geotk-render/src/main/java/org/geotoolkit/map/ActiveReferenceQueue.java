/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Johann Sorel (Geomatys)
 */
public class ActiveReferenceQueue extends ReferenceQueue implements Runnable{
    private static ActiveReferenceQueue singleton = null;

    public static ActiveReferenceQueue getInstance(){
        if(singleton==null)
            singleton = new ActiveReferenceQueue();
        return singleton;
    }

    private ActiveReferenceQueue(){
        Thread t = new Thread(this, "ActiveReferenceQueue");
        t.setDaemon(false);
        t.start();
    }

    public void run(){
        for(;;){
            try{
                Reference ref = super.remove(0);
                if(ref instanceof Runnable){
                    try{
                        ((Runnable)ref).run();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}


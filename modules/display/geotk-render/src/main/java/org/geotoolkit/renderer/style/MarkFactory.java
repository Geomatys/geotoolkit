/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.renderer.style;

import java.awt.Shape;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display.PortrayalException;

/**
 * Build mark shapes.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class MarkFactory {

    /**
     *
     * @param format : defined in ExternalMark
     * @param markRef : WKT Make name or ExternalMark OnlineResource
     * @param markIndex : defined in ExternalMark
     * @return Mark shape or null is factory does not handle the parameters
     */
    public abstract Shape evaluateShape(String format, Object markRef, int markIndex) throws PortrayalException;

    /**
     * Split a path in N parts :
     * 0 : protocol
     * 1 : main path
     * 2+ : arguments as entries
     *
     * @param path
     * @return
     */
    protected static List<Object> splitPath(String path){
        final List<Object> splits = new ArrayList<>();
        final int protocolSplit = path.indexOf(':');
        if(protocolSplit>=0){
            splits.add(path.substring(0, protocolSplit));
            path = path.substring(protocolSplit+1);
        }else{
            splits.add(null);
        }

        final int argsSplit = path.indexOf('?');
        if(argsSplit>=0){
            splits.add(path.substring(0, argsSplit));
            String args = path.substring(argsSplit+1);
            String[] parts = args.split("&");
            for(String part : parts){
                final String[] split = part.split("=");
                final String value = (split.length>1) ? split[1] : null;
                splits.add(new AbstractMap.SimpleImmutableEntry<>(split[0],value));
            }

        }else{
            splits.add(path);
        }

        return splits;
    }


}


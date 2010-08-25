/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.service;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapContext;

/**
 * Scene definition contain of the graphic object that will be
 * rendered in the portrayal service.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SceneDef {

    private final List<PortrayalExtension> extensions = new ArrayList<PortrayalExtension>(){

        @Override
        public boolean add(PortrayalExtension e) {
            if(e == null) return false;
            return super.add(e);
        }

    };
    private Hints hints = new Hints();
    private MapContext context = null;

    public SceneDef() {
    }

    public SceneDef(MapContext context) {
        this(context,null,(List<? extends PortrayalExtension>)null);
    }

    public SceneDef(MapContext context, Hints hints, List<? extends PortrayalExtension> extensions) {
        setContext(context);
        setHints(hints);
        if(extensions != null){
            extensions().addAll(extensions);
        }
    }

    public SceneDef(MapContext context, Hints hints, PortrayalExtension ... extensions) {
        setContext(context);
        setHints(hints);
        for(PortrayalExtension pe : extensions){
            if(pe != null){
                extensions().add(pe);
            }
        }
    }


    public MapContext getContext() {
        return context;
    }

    public void setContext(MapContext context) {
        this.context = context;
    }

    public Hints getHints() {
        return hints;
    }

    public void setHints(Hints hints) {
        this.hints = hints;
    }

    public List<PortrayalExtension> extensions() {
        return extensions;
    }

    @Override
    public String toString() {
        return "SceneDef[context=" + context +"]";
    }

}

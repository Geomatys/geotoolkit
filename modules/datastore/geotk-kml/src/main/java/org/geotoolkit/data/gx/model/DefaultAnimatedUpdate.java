/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.gx.model;

import java.util.List;
import org.geotoolkit.data.kml.model.DefaultAbstractObject;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.gx.xml.GxConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAnimatedUpdate extends DefaultAbstractObject implements AnimatedUpdate {

    private double duration;
    private Update update;

    public DefaultAnimatedUpdate(){
        this.duration = DEF_DURATION;
    }

    public DefaultAnimatedUpdate(List<SimpleTypeContainer> objectSimpleExtensions,
        IdAttributes idAttributes, double duration, Update update){
        super(objectSimpleExtensions, idAttributes);
        this.duration = duration;
        this.update = update;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getDuration() {
        return this.duration;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Update getUpdate() {
        return this.update;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdate(Update update) {
        this.update = update;
    }

}

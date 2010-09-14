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
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.DefaultAbstractObject;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.gx.xml.GxConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultFlyTo extends DefaultAbstractObject implements FlyTo {

    private double duration;
    private EnumFlyToMode flyToMode;
    private AbstractView view;

    public DefaultFlyTo(){
        super();
        this.duration = DEF_DURATION;
        this.flyToMode = DEF_FLY_TO_MODE;
    }

    public DefaultFlyTo(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, double duration,
            EnumFlyToMode flyToMOde, AbstractView view){
        super(objectSimpleExtensions, idAttributes);
        this.duration = duration;
        this.flyToMode = flyToMOde;
        this.view = view;
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
    public EnumFlyToMode getFlyToMode() {
        return this.flyToMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractView getView() {
        return this.view;
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
    public void setFlyToMode(EnumFlyToMode flyToMode) {
        this.flyToMode = flyToMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setView(AbstractView view) {
        this.view = view;
    }

}

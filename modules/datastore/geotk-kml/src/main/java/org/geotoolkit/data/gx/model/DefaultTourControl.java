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
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.gx.xml.GxConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultTourControl extends DefaultAbstractObject implements TourControl {

    private EnumPlayMode playMode;

    public DefaultTourControl(){
        this.playMode = DEF_PLAY_MODE;
    }

    public DefaultTourControl(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, EnumPlayMode playMode){
        super(objectSimpleExtensions, idAttributes);
        this.playMode = playMode;
    }

    @Override
    public EnumPlayMode getPlayMode() {
        return this.playMode;
    }

    @Override
    public void setPlayMode(EnumPlayMode playMode) {
        this.playMode = playMode;
    }

}

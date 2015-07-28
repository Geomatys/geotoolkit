/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.edition;

import javafx.scene.image.Image;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractEditionToolSpi implements EditionTool.Spi {

    private final String name;
    private final InternationalString title;
    private final InternationalString abstrac;
    private final Image icon;

    public AbstractEditionToolSpi(String name, InternationalString title, InternationalString abstrac, Image icon) {
        this.name = name;
        this.title = title;
        this.abstrac = abstrac;
        this.icon = icon;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InternationalString getTitle() {
        return title;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InternationalString getAbstract() {
        return abstrac;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Image getIcon() {
        return icon;
    }

}

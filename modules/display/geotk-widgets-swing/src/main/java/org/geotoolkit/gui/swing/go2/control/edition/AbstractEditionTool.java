/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.go2.control.edition;

import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractEditionTool implements EditionTool{

    private final String name;
    private final InternationalString title;
    private final InternationalString desc;
    private final Class clazz;

    public AbstractEditionTool(final String name, final InternationalString title,
            final InternationalString desc, final Class clazz) {
        this.name = name;
        this.title = title;
        this.desc = desc;
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InternationalString getTitle() {
        return title;
    }

    @Override
    public InternationalString getAbstract() {
        return desc;
    }

    @Override
    public boolean canHandle(Object candidate) {
        return clazz.isInstance(candidate);
    }

    @Override
    public String toString(){
        return getTitle().toString();
    }

}

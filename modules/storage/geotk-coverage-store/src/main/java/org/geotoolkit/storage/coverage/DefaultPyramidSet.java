/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.geotoolkit.gui.swing.tree.Trees;
import org.apache.sis.util.Classes;

/**
 * Default PyramidSet.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultPyramidSet extends AbstractPyramidSet{

    private final String id = UUID.randomUUID().toString();
    private final List<Pyramid> pyramids = new ArrayList<Pyramid>();
    private final List<String> formats = new ArrayList<String>();

    @Override
    public Collection<Pyramid> getPyramids() {
        return pyramids;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> getFormats() {
        return formats;
    }

    @Override
    public String toString(){
        return Trees.toString(Classes.getShortClassName(this)+" "+getId(), getPyramids());
    }

}

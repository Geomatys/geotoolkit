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

package org.geotoolkit.display2d.style.labeling;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLabelLayer implements LabelLayer{

    private final List<LabelDescriptor> labels = new ArrayList<LabelDescriptor>();
    private final boolean obstacle;
    private final boolean labelled;

    public DefaultLabelLayer(boolean isObstacle, boolean isLabelled) {
        this.labelled = isLabelled;
        this.obstacle = isObstacle;
    }

    @Override
    public boolean isObstacle() {
        return obstacle;
    }

    @Override
    public boolean isLabelled() {
        return labelled;
    }

    @Override
    public List<LabelDescriptor> labels() {
        return labels;
    }

}

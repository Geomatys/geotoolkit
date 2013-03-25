/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.process.chain.model;

import java.awt.image.RenderedImage;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.sis.util.collection.UnmodifiableArrayList;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;

/**
 * List of allowed chain data types for input and output parameters.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ChainDataTypes {

    public static final List<Class> EDITABLE_TYPE = (List<Class>)UnmodifiableArrayList.wrap(
                (Class)String.class,
                Boolean.class,
                Integer.class,
                Double.class,
                boolean[].class,
                int[].class,
                double[].class,
                String[].class,
                Map.class,
                Metadata.class,
                Filter.class
            );

    public static final List<Class> VALID_TYPES = (List<Class>)UnmodifiableArrayList.wrap(
                (Class)String.class,
                Boolean.class,
                Integer.class,
                Double.class,
                boolean[].class,
                int[].class,
                double[].class,
                String[].class,
                Map.class,
                List.class,
                Metadata.class,
                Filter.class,
                GridCoverage2D.class,
                RenderedImage.class,
                Date.class,
                Envelope.class,
                Object.class
            );

    private ChainDataTypes(){};

}

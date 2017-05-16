/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.io.plugin.yaml.internal;

import java.util.List;
import org.opengis.coverage.SampleDimension;

/**
 * Contains all attributs which will be shortly written into yaml format.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
public interface YamlWriterBuilder {

    /**
     * Set some {@link SampleDimension} which will be written into yaml files.
     *
     * @param sampleDimensions shortly written sampleDimensions.
     */
    public void setSampleDimensions(final List<SampleDimension> sampleDimensions);
}

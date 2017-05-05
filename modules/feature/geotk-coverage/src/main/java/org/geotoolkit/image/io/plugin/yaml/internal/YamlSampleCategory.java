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

import org.apache.sis.referencing.operation.transform.TransferFunction;
import org.geotoolkit.coverage.Category;
import org.opengis.referencing.operation.MathTransform1D;

/**
 * Equivalent class of {@link Category} adapted for sample data use during Yaml binding.
 *
 * @author Remi Marechal (Geomatys)
 * @since 4.0
 */
public class YamlSampleCategory extends YamlCategory {

    /**
     * Scale value use to build internaly {@link MathTransform1D} sample to geophysic.
     *
     * @see Category#Category(java.lang.CharSequence, java.awt.Color[], int, int, double, double)
     * @see Category#createLinearTransform(double, double)
     */
    private double scale;

    /**
     * Offset value use to build internaly {@link MathTransform1D} sample to geophysic.
     *
     * @see Category#Category(java.lang.CharSequence, java.awt.Color[], int, int, double, double)
     * @see Category#createLinearTransform(double, double)
     */
    private double offset;

    /**
     * Constructor only use during Yaml binding.
     */
    public YamlSampleCategory() {
    }

    /**
     * Build a {@link YamlCategory} from geotk {@link Category}.
     *
     * @param category {@link Category} which will be serialized into Yaml format.
     */
    YamlSampleCategory(final Category category) {
        super(category);

        final MathTransform1D mtSToGeo = category.getSampleToGeophysics();
        //-- peut etre mettre un log si la function de transformation est null .
        if (mtSToGeo != null) {
            final TransferFunction tf = new TransferFunction();
            tf.setTransform(mtSToGeo);

            scale  = tf.getScale();
            offset = tf.getOffset();
        }
    }

    /**
     * Returns needed scale value to build sample to geophysic mathematic functions.
     *
     * @return scale
     * @see #scale
     */
    public double getScale() {
        return scale;
    }

    /**
     * Returns needed offset value to build sample to geophysic mathematic functions.
     *
     * @return offset
     * @see #offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * Set needed scale value to build sample to geophysic mathematic functions.
     *
     * @param offsetZ
     * @see #scale
     */
    public void setOffset(double offsetZ) {
        this.offset = offsetZ;
    }

    /**
     * Set needed offset value to build sample to geophysic mathematic functions.
     *
     * @param scaleZ
     * @see #offset
     */
    public void setScale(double scaleZ) {
        this.scale = scaleZ;
    }
}

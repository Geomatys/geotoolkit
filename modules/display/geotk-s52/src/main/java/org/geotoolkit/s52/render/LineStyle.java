/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.render;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.s52.dai.LinestyleColorReference;
import org.geotoolkit.s52.dai.LinestyleDefinition;
import org.geotoolkit.s52.dai.LinestyleExposition;
import org.geotoolkit.s52.dai.LinestyleVector;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class LineStyle {

    /** holds the color map */
    public LinestyleColorReference colors;
    /** style definition */
    public LinestyleDefinition definition;
    /** explication of the style */
    public LinestyleExposition explication;
    /** drawing commands */
    public final List<LinestyleVector> vectors = new ArrayList<>();

}

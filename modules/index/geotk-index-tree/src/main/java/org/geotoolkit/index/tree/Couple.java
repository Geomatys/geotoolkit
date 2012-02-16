/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

/**Create type B Object couple.
 * 
 * @author Rémi Maréchal (Geomatys).
 */
public interface Couple<B> {

    /**
     * @return Object 1.
     */
    B getObject1();

    /**
     * @return Object2.
     */
    B getObject2();

    /**
     * @return two object perimeter.
     */
    double getPerimeter();

    /**
     * @return true if the two object intersect them.
     */
    boolean intersect();

    /**
     * @return distance between two objects centroids.
     */
    double getDistance();
    
    /**
     * @return two objects area.
     */
    double getArea();
    
    /**
     * @return  Overlaps between two Object.
     */
    double getOverlaps();
}

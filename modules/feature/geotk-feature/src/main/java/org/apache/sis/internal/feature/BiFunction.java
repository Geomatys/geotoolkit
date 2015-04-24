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
package org.apache.sis.internal.feature;

/**
 * Interface sharing the same signature as JDK8 BiFunction.
 * Will be removed when GeotoolKit uses JDK8.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface BiFunction<T, U, R> {

    R apply(T t, U u);
    
}

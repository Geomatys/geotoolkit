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
package org.geotoolkit.filter.binding;

/**
 * Bindings are used by expressions to match propertynames.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Binding<C> {

    /**
     * The priority is used to determinate in which order the bindings are tested.
     * Bindings with higher values will be tested first.
     * @return priority, higher value for higher priority.
     */
    int getPriority();

    /**
     * Binding expected object class.
     * An error will be raised by the get/set methods if an inappropriate object
     * is passed.
     * @return Class, expected binded class, never null.
     */
    Class<C> getBindingClass();

    /**
     * Test if this binding support the given path.
     * @param xpath path to test
     * @return true if given path is supported.
     */
    boolean support(String xpath);

    /**
     * Get value of given object for given path.
     * @param <T> , wanted return class
     * @param candidate , object to evaluate
     * @param xpath , search value path
     * @param target , expected returned class, null for original type.
     * @return value, may be null
     * @throws IllegalArgumentException, if class or path is not supported.
     */
    <T> T get(C candidate, String xpath, Class<T> target) throws IllegalArgumentException;

    /**
     * Set value of given object for given path.
     * @param candidate , object to evaluate
     * @param xpath , search value path
     * @param value , value to set
     * @throws IllegalArgumentException, if class or path is not supported.
     */
    void set(C candidate, String xpath, Object value) throws IllegalArgumentException;

}

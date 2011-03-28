/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.atom.model;

import java.net.URI;
import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultAtomPersonConstruct implements AtomPersonConstruct {

    private List<Object> params;

    /**
     * 
     */
    public DefaultAtomPersonConstruct() {
        this.params = EMPTY_LIST;
    }

    /**
     *
     * @param params
     */
    public DefaultAtomPersonConstruct(final List<Object> params) {
        this.params = (params == null) ? EMPTY_LIST : verifParams(params);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getParams() {
        return this.params;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setParams(final List<Object> params) {
        this.params = (params == null) ? EMPTY_LIST : verifParams(params);
    }

    /**
     * <p>THis method checks parameters class.</p>
     *
     * @param params
     * @return
     */
    private List<Object> verifParams(List<Object> params) {
        for (Object param : params) {
            if (!(param instanceof String) && !(param instanceof AtomEmail)
                    && !(param instanceof URI)) {
                throw new IllegalArgumentException(
                        "This list must content only String," +
                        " URI or AtomEmail instances.");
            }
        }
        return params;
    }
}

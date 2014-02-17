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
package org.geotoolkit.atom;

import java.util.List;
import org.geotoolkit.atom.model.AtomEmail;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface AtomFactory {

    /**
     *
     * @param href
     * @param rel
     * @param type
     * @param hreflang
     * @param title
     * @param length
     * @return
     */
    AtomLink createAtomLink(String href, String rel, String type,
            String hreflang, String title, String length);

    /**
     *
     * @return
     */
    AtomLink createAtomLink();

    /**
     *
     * @param params
     * @return
     */
    AtomPersonConstruct createAtomPersonConstruct(List<Object> params);

    /**
     *
     * @return
     */
    AtomPersonConstruct createAtomPersonConstruct();

    /**
     *
     * @param address
     * @return
     */
    AtomEmail createAtomEmail(String address);

    /**
     *
     * @return
     */
    AtomEmail createAtomEmail();
}

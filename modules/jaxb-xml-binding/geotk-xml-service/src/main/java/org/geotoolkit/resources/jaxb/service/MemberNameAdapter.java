/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.resources.jaxb.service;

import org.geotoolkit.internal.jaxb.metadata.direct.MetadataAdapter;
import org.geotoolkit.naming.DefaultMemberName;
import org.opengis.util.MemberName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class MemberNameAdapter extends MetadataAdapter<MemberName, DefaultMemberName> {

    @Override
    public DefaultMemberName marshal(MemberName value) throws Exception {
        return (value == null || value instanceof DefaultMemberName) ? (DefaultMemberName) value : null;
    }
}

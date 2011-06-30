/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.referencing.crs;

import java.util.Set;
import java.util.Collection;
import java.util.Collections;

import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.util.InternationalString;
import org.opengis.util.GenericName;


/**
 * A placeholder for a class referenced in the {@code geotk-metadata} module while actually defined
 * in the {@code geotk-referencing} module. This class is not necessary when the user don't want to
 * marshall XML for a metadata having CRS objects. However if the user want to do so, then he needs
 * to include the {@code geotk-referencing} module in the classpath otherwise he may get a
 * {@link NoClassDefFoundError}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public class DefaultVerticalCRS implements VerticalCRS {
    private DefaultVerticalCRS() {
    }

    public static DefaultVerticalCRS castOrCopy(VerticalCRS crs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VerticalCS getCoordinateSystem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VerticalDatum getDatum() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReferenceIdentifier getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<GenericName> getAlias() {
        return Collections.emptySet();
    }

    @Override
    public Set<ReferenceIdentifier> getIdentifiers() {
        return Collections.emptySet();
    }

    @Override
    public Extent getDomainOfValidity() {
        return null;
    }

    @Override
    public InternationalString getScope() {
        return null;
    }

    @Override
    public InternationalString getRemarks() {
        return null;
    }

    @Override
    public String toWKT() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.referencing;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.metadata.Identifier;

import org.apache.sis.metadata.iso.ImmutableIdentifier;

import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


/**
 * Identifier for a 3D CRS built from a 2D horizontal component and a 1D vertical component. This
 * identifier is created by {@link org.geotoolkit.referencing.factory.ReferencingFactoryContainer}
 * in order to allow the {@link org.geotoolkit.referencing.operation.AuthorityBackedFactory} class
 * to find the original 2D component of a monolithic (not a compound) 3D CRS.
 * <p>
 * This class is a hack. It exists for the following reasons:
 * <p>
 * <ul>
 *   <li>We can not just rebuilt a new 2D CRS from a 3D one, because the EPSG code would be lost.
 *       We need the EPSG code of the 2D component for {@code AuthorityBackedFactory} work.</li>
 *   <li>We can not let the 3D (horizontal + ellipsoidal height) CRS as a {@code ComponentCRS}
 *       because the {@code DefaultCoordinateOperationFactory} processing chain needs to pass
 *       those 3D CRS as monolithic objects. This is because some transformation steps need to
 *       process the three dimensions together; the ellipsoidal height shall never be separated
 *       from the geographical coordinates.</li>
 * </ul>
 * <p>
 * This hack should not be visible in public API, and may change in incompatible way in any future
 * version.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @see org.geotoolkit.referencing.operation.AuthorityBackedFactory#getHorizontalCRS
 *
 * @since 3.16
 * @module
 */
public final class Identifier3D extends ImmutableIdentifier {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1799606724741505296L;

    /**
     * The horizontal component of the 3D CRS identified by this object.
     * It must be a CRS backed by a geodetic datum.
     */
    public final SingleCRS horizontalCRS;

    /**
     * Creates a new identifier with the same values than the given identifier,
     * and store the given horizontal component for information purpose.
     *
     * @param identifier    The identifier to copy.
     * @param horizontalCRS The horizontal component of the 3D CRS.
     */
    private Identifier3D(final Identifier identifier, final SingleCRS horizontalCRS) {
        super(identifier);
        this.horizontalCRS = horizontalCRS;
        assert horizontalCRS.getDatum() instanceof GeodeticDatum : horizontalCRS;
    }

    /**
     * Overridden for consistency with {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return super.hashCode() + 31 * horizontalCRS.hashCode();
    }

    /**
     * Compares this object with the given one for equality. This method needs to take
     * the horizontal CRS in account, because CRS using this identifier may be stored
     * in {@link org.geotoolkit.util.collection.WeakHashSet}.
     */
    @Override
    public boolean equals(final Object object) {
        if (super.equals(object)) {
            final Identifier3D other = (Identifier3D) object;
            return Objects.equals(horizontalCRS, other.horizontalCRS);
        }
        return false;
    }

    /**
     * Given the properties of a CRS, replaces the value of the {@code "name"} key by an
     * {@code Identifier3D} instance declaring the given horizontal CRS.
     *
     * @param  properties    The properties of a CRS.
     * @param  horizontalCRS The horizontal component of the 3D CRS.
     * @return The properties with the given horizontal CRS injected in the name identifier.
     */
    public static Map<String,?> addHorizontalCRS(Map<String,?> properties, final SingleCRS horizontalCRS) {
        final Map<String,Object> copy = new HashMap<>(properties);
        final Identifier id = (Identifier) copy.get(NAME_KEY);
        copy.put(NAME_KEY, new Identifier3D(id, horizontalCRS));
        return copy;
    }
}

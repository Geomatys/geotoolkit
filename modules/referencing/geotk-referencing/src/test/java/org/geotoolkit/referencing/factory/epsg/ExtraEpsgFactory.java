/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.io.IOException;
import org.geotoolkit.metadata.Citations;


/**
 * Extra class used to make sure we have {@link PropertyEpsgFactory} among the fallbacks
 * (used to check the fallback mechanism). This factory is registered programmatically by
 * {@link FallbackAuthorityFactoryTest}.
 *
 * @author Andrea Aime (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 */
final strictfp class ExtraEpsgFactory extends PropertyEpsgFactory {
    /**
     * Creates a factory to be registered before {@link FactoryUsingWKT} in the fallback chain.
     */
    public ExtraEpsgFactory() throws IOException {
        super(EMPTY_HINTS, "extra-epsg.properties", Citations.EPSG);
    }

    /**
     * Makes sure we are before {@link PropertyEpsgFactory} in the fallback chain.
     */
    @Override
    protected void setOrdering(final Organizer organizer) {
        super.setOrdering(organizer);
        organizer.before(PropertyEpsgFactory.class, false);
    }
}

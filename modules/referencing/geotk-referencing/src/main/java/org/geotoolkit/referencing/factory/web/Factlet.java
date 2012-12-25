/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.web;

import java.util.Collections;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import org.apache.sis.measure.Units;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.resources.Errors;


/**
 * Mini Plug-In API for {@linkplain ProjectedCRS projected CRS} from the {@code AUTO} authority.
 *
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 2.2
 * @module
 */
@Immutable
abstract class Factlet {
    /**
     * Returns the {@code AUTO} code for this plugin.
     */
    public abstract int code();

    /**
     * Returns the name for the CRS to be created by this plugin.
     */
    public abstract String getName();

    /**
     * Returns the classification of projection method.
     * For example {@code "Transverse_Mercator"}.
     */
    public abstract String getClassification();

    /**
     * Creates a coordinate reference system from the specified code. The default
     * implementation creates a {@linkplain ParameterValueGroup parameter group}
     * for the {@linkplain #getClassification projection classification}, and then
     * invokes {@link #setProjectionParameters} in order to fill the parameter values.
     */
    public final ProjectedCRS create(final Code code, final ReferencingFactoryContainer factories)
            throws FactoryException
    {
        DefaultCartesianCS cs = DefaultCartesianCS.PROJECTED;
        final Unit<?> unit = code.unit;
        if (!SI.METRE.equals(unit)) {
            if (!Units.isLinear(unit)) {
                throw new NoSuchAuthorityCodeException(Errors.format(
                        Errors.Keys.NON_LINEAR_UNIT_$1, unit), code.authority,
                        String.valueOf(code.code), code.toString());
            }
            cs = cs.usingUnit(unit);
        }
        final String classification = getClassification();
        final ParameterValueGroup parameters;
        parameters = factories.getMathTransformFactory().getDefaultParameters(classification);
        setProjectionParameters(parameters, code);
        final String name = getName();
        final DefiningConversion conversion = new DefiningConversion(name, parameters);
        return factories.getCRSFactory().createProjectedCRS(
                Collections.singletonMap(IdentifiedObject.NAME_KEY, name),
                DefaultGeographicCRS.WGS84, conversion, cs);
    }

    /**
     * Fills the parameter values for the specified code.
     */
    protected abstract void setProjectionParameters(ParameterValueGroup parameters, Code code);
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.internal.feature;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.iso.Names;
import org.opengis.feature.FeatureType;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;

/**
 * Common feature type used in geotoolkit.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TypeConventions {

    /**
     * Scope of all names defined by GeotoolKit convention.
     */
    private static final LocalName SCOPE;

    /**
     * Conventional name for a property used for coverage range elements.
     *
     * <p>Properties of this name must be a feature association role.</p>
     */
    public static final ScopedName RANGE_ELEMENTS_PROPERTY;

    /**
     * Abstract feature type for a Coverage.
     */
    public static final FeatureType COVERAGE_TYPE;
    /**
     * Abstract feature type for a 'GeometryValuePair' in the coverage.<br>
     * In case of a grid images in RGB, this could be called a pixel.
     */
    public static final FeatureType COVERAGE_RECORD_TYPE;



    static {

        SCOPE = Names.createLocalName("GeotoolKit", null, "geotk");
        RANGE_ELEMENTS_PROPERTY = Names.createScopedName(SCOPE, null, "rangeElements");

        final FeatureTypeBuilder rftb = new FeatureTypeBuilder();
        rftb.setName(Names.createScopedName(SCOPE, null, "CoverageRecord"));
        rftb.setAbstract(true);
        rftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setMinimumOccurs(1).setMaximumOccurs(1).addRole(AttributeRole.DEFAULT_GEOMETRY);
        COVERAGE_RECORD_TYPE = rftb.build();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(Names.createScopedName(SCOPE, null, "Coverage"));
        ftb.setAbstract(true);
        ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setMinimumOccurs(1).setMaximumOccurs(1).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAssociation(COVERAGE_RECORD_TYPE).setName(RANGE_ELEMENTS_PROPERTY).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).build();
        COVERAGE_TYPE = ftb.build();

    }

    private TypeConventions(){}
    
}

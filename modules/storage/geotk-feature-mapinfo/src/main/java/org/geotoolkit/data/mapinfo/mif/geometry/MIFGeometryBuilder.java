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
package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.util.Collections;
import org.apache.sis.storage.DataStoreException;
import org.opengis.util.GenericName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.feature.builder.AttributeRole;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.InvalidPropertyValueException;
import org.opengis.feature.PropertyNotFoundException;

/**
 * Used for {@link org.geotoolkit.data.mapinfo.mif.MIFUtils.GeometryType}, to allow us pass a method for building geometry as
 * enum attribute.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public abstract class MIFGeometryBuilder {

    protected final static Logger LOGGER = Logging.getLogger("org.geotoolkit.data.mapinfo.mif.geometry");
    protected final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel());

    protected static final AttributeType STRING_TYPE = new DefaultAttributeType(Collections.singletonMap("name", "TEXT"), String.class, 1, 1, null);

    /**
     * Parse an input file to build a JTS Geometry with its data.
     *
     *
     * @param scanner the Scanner to use for geometry parsing (should be placed on the beginning of the geometry).
     * @param toFill The feature to put geometry data. It cannot be null, and should have been built with feature type
     *               given by {@link MIFGeometryBuilder#buildType(org.opengis.referencing.crs.CoordinateReferenceSystem, org.opengis.feature.type.FeatureType)}.
     * @param toApply
     */
    public abstract void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException;

    /**
     * Build a feature type which represents a MIF geometry.
     *
     *
     *
     * @param crs The CRS to put in feature type. If null, no CRS will be pass to the feature type.
     * @param parent
     * @return A {@link org.geotoolkit.feature.FeatureType} which describe a geometry (as MIF defines it).
     */
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        FeatureTypeBuilder builder = new FeatureTypeBuilder();

        // As parent's attributes are not shared, we must copy them to the new feature type.
        boolean addGeometry = true;
        final GenericName name;
        if (parent != null) {
            name = getName().push(parent.getName());
            // Check if there's already a geometric property.
            try {
                FeatureExt.getDefaultGeometry(parent);
                addGeometry = false;
            } catch (PropertyNotFoundException e) {
                LOGGER.log(Level.FINEST, "no geometry found in parent data type", e);
            }

            builder.setSuperTypes(parent);
            for (AttributeType desc : getAttributes()) {
                if (!parent.getProperties(true).contains(desc)) {
                    builder.addAttribute(desc);
                }
            }
        } else {
            name = getName();
        }

        // If parent type has no geometry, we add one.
        if (addGeometry) {
            builder.addAttribute(getGeometryBinding()).setName(getName()).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        }

        builder.setName(name);
        return builder.build();
    }

    /**
     * Build a MIF geometry from the current Geotk feature.
     *
     * For the moment, MIF style generation is not managed. However, base implementation in {@link MIFGeometryBuilder}
     * only process base verification on the input feature (valid geometry type).
     *
     * @param feature The feature to read for MIF geometry building.
     * @return A String which is the representation of the MIF geometry.
     * @throws DataStoreException If an error occur while reading feature.
     */
    public String toMIFSyntax(Feature feature) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source geometry", feature);
        if (!FeatureExt.hasAGeometry(feature.getType())) {
            throw new DataStoreException("Input feature does not contain any geometry.");
        } else {
            final Object valueObj = MIFUtils.getGeometryValue(feature);
            boolean geomMatch = false;
            Class geomCls = null;
            if(valueObj !=null){
                geomCls = valueObj.getClass();
                for(Class possibleBinding : getPossibleBindings()) {
                    if (geomCls.equals(possibleBinding)) {
                        geomMatch = true;
                        break;
                    }
                }
            }
            if(!geomMatch) {
                throw new DataStoreException("Input feature does not contain the right geometry type.\nExpected : "
                        +getGeometryBinding()+"\nFound : "+geomCls);
            }
        }
        return new String();
    }

    /**
     * @return the default java class used to store the MIF geometry.
     */
    public abstract Class getGeometryBinding();

    /**
     * @return all possible geometry class we can use to represent our geometry.
     */
    public abstract Class[] getPossibleBindings();

    /**
     * The name of the geometry, as MIF defines it.
     * @return
     */
    public abstract GenericName getName();

    protected abstract List<AttributeType> getAttributes();

    public Stream<AttributeType> attributes() {
        return getAttributes().stream();
    }

    public Geometry getGeometry(final Feature f) throws PropertyNotFoundException, InvalidPropertyValueException {
        final Object geom = f.getPropertyValue(getName().toString());
        if (geom instanceof Geometry || geom == null)
            return (Geometry) geom;
        throw new InvalidPropertyValueException("Geometric property contains unknown data of type ".concat(geom.getClass().toString()));
    }
}

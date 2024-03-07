/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.feature;

import org.locationtech.jts.geom.Geometry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Deprecable;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Mapper which change all geometry attributes CoordinateReferenceSystem.
 * All geometric properties are transformed not just the default geometry.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ReprojectMapper extends FeatureSetMapper {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature");

    private final CoordinateReferenceSystem targetCRS;
    private final Set<String> fullNames = new HashSet<>();
    private final Map<String,CoordinateReferenceSystem> geomNames = new HashMap<>();
    private final FeatureType mapped;
    private final Map<CoordinateReferenceSystem,GeometryCSTransformer> cache = new HashMap<>();

    /**
     * Filter feature type properties.
     *
     * @param base reference feature type
     * @param targetCRS wanted CoordinateReferenceSystem
     */
    public ReprojectMapper(FeatureType base, final CoordinateReferenceSystem targetCRS) {
        this.targetCRS = targetCRS;

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(base.getName());
        ftb.setDefinition(base.getDefinition());
        ftb.setDescription(base.getDescription());
        ftb.setDesignation(base.getDesignation());
        ftb.setDeprecated(base instanceof Deprecable && ((Deprecable)base).isDeprecated());

        for (PropertyType property : base.getProperties(true)) {

            //try to preserve basic operations
            if (property instanceof Operation) {
                String code = ((Operation) property).getParameters().getName().getCode();
                if ("Envelope".equals(code) || "Link".equals(code)) {
                    ftb.addProperty(property);
                    continue;
                }
            }

            fullNames.add(property.getName().toString());


            //unroll operation
            if (property instanceof Operation) {
                final PropertyType baseProperty = property;
                while (property instanceof Operation) {
                    property = (PropertyType) ((Operation)property).getResult();
                }
                //we must preserve the original operation name.
                final PropertyTypeBuilder typeBuilder = ftb.addProperty(property);
                property = (PropertyType) typeBuilder.setName(baseProperty.getName()).build();
                ftb.properties().remove(typeBuilder);
            }

            if (AttributeConvention.isGeometryAttribute(property)) {
                final AttributeType res = (AttributeType) property;
                final CoordinateReferenceSystem declatedCrs = FeatureExt.getCRS(res);
                ftb.addAttribute(res).setCRS(targetCRS);

                geomNames.put(property.getName().toString(),declatedCrs);
            } else {
                ftb.addProperty(property);
            }
        }

        fixOperations(ftb);
        mapped = ftb.build();
    }

    @Override
    public FeatureType getMappedType() {
        return mapped;
    }

    @Override
    public Feature apply(Feature t) {
        final Feature feature = mapped.newInstance();
        for (String name : fullNames) {
            Object value = t.getPropertyValue(name);
            if (geomNames.containsKey(name) && value != null) {
                try {
                    final CoordinateReferenceSystem declaredCrs = geomNames.get(name);
                    value = reproject((Geometry)value, declaredCrs);
                } catch (TransformException | FactoryException ex) {
                    //TODO replace by a runtime exception from SIS, which one ?
                    throw new RuntimeException(ex.getMessage(),ex);
                }
            }
            feature.setPropertyValue(name, value);
        }
        return feature;
    }

    private Geometry reproject(Geometry val, CoordinateReferenceSystem declaredCrs) throws FactoryException, TransformException {
        if (val == null) return val;

        if (declaredCrs == null) {
            //extract the crs from the geometry
            try {
                declaredCrs = JTS.findCoordinateReferenceSystem((Geometry)val);
                if(declaredCrs==targetCRS) return val;
            } catch (FactoryException ex) {
                LOGGER.log(Level.FINE, "Cannot extract CRS from geometry", ex);
                //we don't know the original crs, we can't transform
                return val;
            }
        }

        if (declaredCrs == null) {
            LOGGER.log(Level.WARNING,
                    "A feature geometry property in type "+mapped.getName() +" has no crs.");
            return val;
        }

        GeometryCSTransformer trs = cache.get(declaredCrs);
        if (trs == null) {
            final CoordinateSequenceMathTransformer cstrs =
                    new CoordinateSequenceMathTransformer(CRS.findOperation(declaredCrs, targetCRS, null).getMathTransform());
            trs = new GeometryCSTransformer(cstrs);
            cache.put(declaredCrs, trs);
        }

        final Geometry geom = trs.transform(val);
        JTS.setCRS(geom, targetCRS);
        return geom;
    }
}

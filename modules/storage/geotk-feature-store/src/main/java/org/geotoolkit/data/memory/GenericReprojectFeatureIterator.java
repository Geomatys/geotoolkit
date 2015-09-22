/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.memory;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.DefaultFeature;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.geotoolkit.referencing.CRS;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.Classes;
import static org.geotoolkit.data.memory.GenericTransformFeatureIterator.FF;
import org.apache.sis.util.logging.Logging;
import org.opengis.coverage.Coverage;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.GeometryAttribute;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Basic support for a  FeatureIterator that reprojects the geometry attribute.
 *
 * @author Chris Holmes
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericReprojectFeatureIterator<R extends FeatureReader>
                        extends GenericTransformFeatureIterator<R> implements FeatureReader{

    protected final FeatureType schema;
    protected final CoordinateReferenceSystem targetCRS;

    /**
     * Creates a new instance of GenericReprojectFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param maxFeatures maximum number of feature
     */
    private GenericReprojectFeatureIterator(final R iterator, final CoordinateReferenceSystem targetCRS)
                            throws FactoryException, MismatchedFeatureException {
        super(iterator,findTransformer(iterator.getFeatureType(), targetCRS));

        final FeatureType type = iterator.getFeatureType();
        this.targetCRS = targetCRS;
        this.schema = FeatureTypeUtilities.transform(type, targetCRS);
    }


    @Override
    public FeatureType getFeatureType() {
        return schema;
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    private static GeometryTransformer findTransformer(final FeatureType type,
            final CoordinateReferenceSystem targetCRS) throws FactoryException{
        ensureNonNull("crs", targetCRS);

        final CoordinateReferenceSystem original = type.getGeometryDescriptor().getCoordinateReferenceSystem();

        if(original != null){
            //the crs is defined on the feature type
            final CoordinateSequenceMathTransformer trs =
                    new CoordinateSequenceMathTransformer(CRS.findMathTransform(original, targetCRS, true));
            GeometryCSTransformer ts = new GeometryCSTransformer(trs);
            ts.setCoordinateReferenceSystem(targetCRS);
            return ts;
        }else{
            return null;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap a FeatureReader with a reprojection.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericReprojectFeatureReader extends GenericReprojectFeatureIterator{

        private GenericReprojectFeatureReader(final FeatureReader reader, final CoordinateReferenceSystem targetCRS) throws FactoryException, MismatchedFeatureException{
            super(reader,targetCRS);
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            final Feature next = iterator.next();

            final Collection<Property> properties = new ArrayList<Property>();
            for(Property prop : next.getProperties()){
                if(prop instanceof GeometryAttribute){
                    Object value = prop.getValue();
                    if(value != null){
                        //create a new property with the projected type
                        prop = FF.createGeometryAttribute(value, (GeometryDescriptor)
                                schema.getDescriptor(prop.getDescriptor().getName()), null, null);

                        if(value instanceof Coverage){
                            value = Operations.DEFAULT.resample((Coverage)value, targetCRS);
                            prop.setValue(value);
                        }else if(transformer != null){
                            //the transform applies to all feature
                            try {
                                prop.setValue(transformer.transform((Geometry) value));
                            } catch (TransformException e) {
                                throw new FeatureStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
                            }
                        }else{
                            //each feature has a different CRS.
                            final CoordinateReferenceSystem original;
                            if(value instanceof Geometry){
                                try {
                                    original = JTS.findCoordinateReferenceSystem((Geometry)value);
                                } catch (NoSuchAuthorityCodeException ex) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                                } catch (FactoryException ex) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                                }
                            }else if(value instanceof org.opengis.geometry.Geometry){
                                original = ((org.opengis.geometry.Geometry)value).getCoordinateReferenceSystem();
                            }else{
                                original = null;
                            }

                            if(original != null){
                                try {
                                    final CoordinateSequenceMathTransformer trs =
                                            new CoordinateSequenceMathTransformer(CRS.findMathTransform(original, targetCRS, true));
                                    final GeometryCSTransformer transformer = new GeometryCSTransformer(trs);
                                    Geometry geom = transformer.transform((Geometry) value);
                                    geom.setSRID(SRIDGenerator.toSRID(targetCRS, SRIDGenerator.Version.V1));
                                    prop.setValue(geom);
                                } catch (Exception e) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", e);
                                }
                            }else{
                                Logging.getLogger("org.geotoolkit.data.memory").log(
                                        Level.WARNING, "A feature in type :"+getFeatureType().getName() +" has no crs.");
                            }
                        }

                    }
                }
                properties.add(prop);
            }

            final Feature f = FF.createFeature(properties, schema, next.getIdentifier().getID());
            f.getUserData().putAll(next.getUserData());
            return f;
        }

    }

    /**
     * Wrap a FeatureReader with a reprojection and reuse the feature each time.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericReuseReprojectFeatureReader extends GenericReprojectFeatureIterator{

        private final Collection<Property> properties;
        private final AbstractFeature feature;

        private GenericReuseReprojectFeatureReader(final FeatureReader reader, final CoordinateReferenceSystem targetCRS)
                                            throws FactoryException, MismatchedFeatureException{
            super(reader, targetCRS);
            feature = new DefaultFeature(Collections.EMPTY_LIST, schema, null);
            properties = feature.getProperties();
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            final Feature next = iterator.next();
            feature.setIdentifier(next.getIdentifier());

            properties.clear();
            for(Property prop : next.getProperties()){
                if(prop instanceof GeometryAttribute){
                    Object value = prop.getValue();
                    if(value != null){
                        //create a new property with the projected type
                        prop = FF.createGeometryAttribute(value, (GeometryDescriptor)
                                schema.getDescriptor(prop.getDescriptor().getName()), null, null);

                        if(value instanceof Coverage){
                            value = Operations.DEFAULT.resample((Coverage)value, targetCRS);
                            prop.setValue(value);
                        }else if(transformer != null){
                            //the transform applies to all feature
                            try {
                                prop.setValue(transformer.transform((Geometry) value));
                            } catch (TransformException e) {
                                throw new FeatureStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
                            }
                        }else{
                            //each feature has a different CRS.
                            final CoordinateReferenceSystem original;
                            if(value instanceof Geometry){
                                try {
                                    original = JTS.findCoordinateReferenceSystem((Geometry)value);
                                } catch (NoSuchAuthorityCodeException ex) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                                } catch (FactoryException ex) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                                }
                            }else if(value instanceof org.opengis.geometry.Geometry){
                                original = ((org.opengis.geometry.Geometry)value).getCoordinateReferenceSystem();
                            }else{
                                original = null;
                            }

                            if(original != null){
                                try {
                                    final CoordinateSequenceMathTransformer trs =
                                            new CoordinateSequenceMathTransformer(CRS.findMathTransform(original, targetCRS, true));
                                    final GeometryCSTransformer transformer = new GeometryCSTransformer(trs);
                                    Geometry geom = transformer.transform((Geometry) value);
                                    geom.setSRID(SRIDGenerator.toSRID(targetCRS, SRIDGenerator.Version.V1));
                                    prop.setValue(geom);
                                } catch (Exception e) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", e);
                                }
                            }else{
                                Logging.getLogger("org.geotoolkit.data.memory").log(
                                        Level.WARNING, "A feature in type :"+getFeatureType().getName() +" has no crs.");
                            }
                        }

                    }
                }
                properties.add(prop);
            }

            feature.getUserData().clear();
            feature.getUserData().putAll(next.getUserData());
            return feature;
        }

    }

    /**
     * Wrap a FeatureReader with a reprojection and reuse the simple feature each time.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericSimpleReuseReprojectFeatureReader extends GenericReprojectFeatureIterator{

        private final Object[] values;
        private final DefaultSimpleFeature feature;
        private final boolean[] geomIndexes;

        private GenericSimpleReuseReprojectFeatureReader(final FeatureReader reader, final CoordinateReferenceSystem targetCRS)
                                            throws FactoryException, MismatchedFeatureException{
            super(reader, targetCRS);

            final SimpleFeatureType ft = (SimpleFeatureType) reader.getFeatureType();
            values = new Object[ft.getAttributeCount()];
            geomIndexes = new boolean[values.length];
            feature = new DefaultSimpleFeature((SimpleFeatureType)schema, null, values, false);

            for(int i=0;i<values.length;i++){
               geomIndexes[i] = ft.getDescriptor(i) instanceof GeometryDescriptor;
            }
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            final SimpleFeature next = (SimpleFeature) iterator.next();
            feature.setId(next.getID());

            for(int i=0;i<values.length;i++){
                if(geomIndexes[i]){
                    Object value = next.getAttribute(i);
                    if(value != null){
                        //create a new property with the projected type

                        if(value instanceof Coverage){
                            values[i] = Operations.DEFAULT.resample((Coverage)value, targetCRS);
                        }else if(transformer != null){
                            //the transform applies to all feature
                            try {
                                values[i] = transformer.transform((Geometry) value);
                            } catch (TransformException e) {
                                throw new FeatureStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
                            }
                        }else{
                            //each feature has a different CRS.
                            final CoordinateReferenceSystem original;
                            if(value instanceof Geometry){
                                try {
                                    original = JTS.findCoordinateReferenceSystem((Geometry)value);
                                } catch (NoSuchAuthorityCodeException ex) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                                } catch (FactoryException ex) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                                }
                            }else if(value instanceof org.opengis.geometry.Geometry){
                                original = ((org.opengis.geometry.Geometry)value).getCoordinateReferenceSystem();
                            }else{
                                original = null;
                            }

                            if(original != null){
                                try {
                                    final CoordinateSequenceMathTransformer trs =
                                            new CoordinateSequenceMathTransformer(CRS.findMathTransform(original, targetCRS, true));
                                    final GeometryCSTransformer transformer = new GeometryCSTransformer(trs);
                                    Geometry geom = transformer.transform((Geometry) value);
                                    JTS.setCRS(geom, targetCRS);
                                    geom.setSRID(SRIDGenerator.toSRID(targetCRS, SRIDGenerator.Version.V1));
                                    values[i] = geom;
                                } catch (Exception e) {
                                    throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", e);
                                }
                            }else{
                                Logging.getLogger("org.geotoolkit.data.memory").log(
                                        Level.WARNING, "A feature in type :"+getFeatureType().getName() +" has no crs.");
                            }
                        }

                    }else{
                        values[i] = null;
                    }

                }else{
                    values[i] = next.getAttribute(i);
                }
            }

            feature.getUserData().clear();
            feature.getUserData().putAll(next.getUserData());
            return feature;
        }

    }



    private static final class GenericReprojectFeatureCollection extends WrapFeatureCollection{

        private final CoordinateReferenceSystem targetCrs;

        private GenericReprojectFeatureCollection(final FeatureCollection original, final CoordinateReferenceSystem targetCrs){
            super(original);
            this.targetCrs = targetCrs;
        }

        @Override
        public FeatureType getFeatureType() {
            try {
                return FeatureTypeUtilities.transform(
                        getOriginalFeatureCollection().getFeatureType(), targetCrs);
            } catch (MismatchedFeatureException ex) {
                Logger.getLogger(GenericReprojectFeatureIterator.class.getName()).log(Level.WARNING, null, ex);
            }
            return super.getFeatureType();
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            FeatureIterator ite = getOriginalFeatureCollection().iterator(hints);
            if(!(ite instanceof FeatureReader)){
                ite = GenericWrapFeatureIterator.wrapToReader(ite, getFeatureType());
            }
            try {
                return wrap((FeatureReader) ite, targetCrs, hints);
            } catch (FactoryException ex) {
                throw new FeatureStoreRuntimeException(ex);
            } catch (MismatchedFeatureException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureReader with a reprojection.
     */
    public static FeatureReader wrap(final FeatureReader reader, final CoordinateReferenceSystem crs, final Hints hints) throws FactoryException, MismatchedFeatureException {
        final GeometryDescriptor desc = reader.getFeatureType().getGeometryDescriptor();
        if (desc != null) {

            final CoordinateReferenceSystem original =desc.getCoordinateReferenceSystem();

            if (CRS.equalsIgnoreMetadata(original, crs)) {
                //no need to wrap it, already in the asked projection
                return reader;
            }

            final Boolean detached = (hints == null) ? null : (Boolean) hints.get(HintsPending.FEATURE_DETACHED);
            if(detached == null || detached){
                //default behavior, make separate features
                return new GenericReprojectFeatureReader(reader, crs);
            }else{
                //reuse same feature
                final FeatureType ft = reader.getFeatureType();
                if(ft instanceof SimpleFeatureType){
                    return new GenericSimpleReuseReprojectFeatureReader(reader, crs);
                }else{
                    return new GenericReuseReprojectFeatureReader(reader, crs);
                }
            }

        } else {
            return reader;
        }
    }

    /**
     * Create a reproject FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final CoordinateReferenceSystem crs){
        return new GenericReprojectFeatureCollection(original, crs);
    }

    public static Feature apply(Feature next, final CoordinateReferenceSystem targetCRS) throws MismatchedFeatureException, FactoryException{

        final FeatureType schema = FeatureTypeUtilities.transform(next.getType(), targetCRS);
        final GeometryTransformer geoTransformer = findTransformer(next.getType(), targetCRS);

        final Collection<Property> properties = new ArrayList<Property>();
        for(Property prop : next.getProperties()){
            if(prop instanceof GeometryAttribute){
                Object value = prop.getValue();
                if(value != null){
                    //create a new property with the projected type
                    prop = FF.createGeometryAttribute(value, (GeometryDescriptor)
                            schema.getDescriptor(prop.getDescriptor().getName()), null, null);

                    if(value instanceof Coverage){
                        value = Operations.DEFAULT.resample((Coverage)value, targetCRS);
                        prop.setValue(value);
                    }else if(geoTransformer != null){
                        //the transform applies to all feature
                        try {
                            prop.setValue(geoTransformer.transform((Geometry) value));
                        } catch (TransformException e) {
                            throw new FeatureStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
                        }
                    }else{
                        //each feature has a different CRS.
                        final CoordinateReferenceSystem original;
                        if(value instanceof Geometry){
                            try {
                                original = JTS.findCoordinateReferenceSystem((Geometry)value);
                            } catch (NoSuchAuthorityCodeException ex) {
                                throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                            } catch (FactoryException ex) {
                                throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", ex);
                            }
                        }else if(value instanceof org.opengis.geometry.Geometry){
                            original = ((org.opengis.geometry.Geometry)value).getCoordinateReferenceSystem();
                        }else{
                            original = null;
                        }

                        if(original != null){
                            try {
                                final CoordinateSequenceMathTransformer trs =
                                        new CoordinateSequenceMathTransformer(CRS.findMathTransform(original, targetCRS, true));
                                final GeometryCSTransformer transformer = new GeometryCSTransformer(trs);
                                Geometry geom = transformer.transform((Geometry) value);
                                geom.setSRID(SRIDGenerator.toSRID(targetCRS, SRIDGenerator.Version.V1));
                                prop.setValue(geom);
                            } catch (Exception e) {
                                throw new FeatureStoreRuntimeException("An exception occurred while reprojecting data on the fly", e);
                            }
                        }else{
                            Logging.getLogger("org.geotoolkit.data.memory").log(
                                    Level.WARNING, "A feature has no crs.");
                        }
                    }

                }
            }
            properties.add(prop);
        }

        final Feature f = FF.createFeature(properties, schema, next.getIdentifier().getID());
        f.getUserData().clear();
        f.getUserData().putAll(next.getUserData());
        return f;
    }

}

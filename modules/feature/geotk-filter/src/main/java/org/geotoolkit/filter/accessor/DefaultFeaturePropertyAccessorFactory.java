/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.accessor;

import java.util.regex.Pattern;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.collection.Cache;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

/**
 * Creates a property accessor for simple features.
 * <p>
 * The created accessor handles a small subset of xpath expressions, a
 * non-nested "name" which corresponds to a feature attribute, and "@id",
 * corresponding to the feature id.
 * </p>
 * <p>
 * THe property accessor may be run against {@link SimpleFeature}, or 
 * against {@link SimpleFeature}. In the former case the feature property 
 * value is returned, in the latter the feature property type is returned. 
 * </p>
 * 
 * @author Justin Deoliveira, The Open Planning Project
 * @module pending
 */
public class DefaultFeaturePropertyAccessorFactory implements PropertyAccessorFactory {

    /** Single instnace is fine - classes are thread safe */
    private static final PropertyAccessor ATTRIBUTE_ACCESS = new SimpleFeaturePropertyAccessor();
    private static final PropertyAccessor DEFAULT_GEOMETRY_ACCESS = new DefaultGeometrySimpleFeaturePropertyAccessor();
    private static final PropertyAccessor FID_ACCESS = new FidSimpleFeaturePropertyAccessor();
    private static final Pattern ID_PATTERN       = Pattern.compile("@(\\w+:)?id");
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\w+:)?(\\w+)");
    private static final Cache<String,PropertyAccessor> CACHE = new Cache<String, PropertyAccessor>();

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyAccessor createPropertyAccessor(Class type, String xpath, Class target, Hints hints) {

        if (xpath == null) {
            return null;
        }

        if (!Feature.class.isAssignableFrom(type) && !FeatureType.class.isAssignableFrom(type)) {
            return null; // we only work with feature
        }



        //try to find the accessor in the cache
        PropertyAccessor accessor = CACHE.peek(xpath);
        if(accessor != null){
            return accessor;
        }

        //if ("".equals(xpath) && target == Geometry.class)
        if (xpath.isEmpty()) {
            final Cache.Handler<PropertyAccessor> handler = CACHE.lock(xpath);
            accessor = handler.peek();
            if (accessor == null) {
                accessor = DEFAULT_GEOMETRY_ACCESS;
            }
            handler.putAndUnlock(accessor);
            return DEFAULT_GEOMETRY_ACCESS;
        }

        //check for fid access
        if (ID_PATTERN.matcher(xpath).matches()) {
            final Cache.Handler<PropertyAccessor> handler = CACHE.lock(xpath);
            accessor = handler.peek();
            if (accessor == null) {
                accessor = FID_ACCESS;
            }
            handler.putAndUnlock(accessor);
            return FID_ACCESS;
        }

        //check for simple property acess
        if (PROPERTY_PATTERN.matcher(xpath).matches()) {
            final Cache.Handler<PropertyAccessor> handler = CACHE.lock(xpath);
            accessor = handler.peek();
            if (accessor == null) {
                accessor = ATTRIBUTE_ACCESS;
            }
            handler.putAndUnlock(accessor);
            return ATTRIBUTE_ACCESS;
        }

        return null;
    }

    /**
     * We strip off namespace prefix, we need new feature model to do this
     * property
     * <ul>
     * <li>BEFORE: foo:bar
     * <li>AFTER: bar
     * </ul>
     * 
     * @param xpath
     * @return xpath with any XML prefixes removed
     */
    private static String stripPrefix(String xpath) {
        final int split = xpath.indexOf(':');
        if (split != -1) {
            return xpath.substring(split + 1);
        }
        return xpath;
    }

    /**
     * Access to SimpleFeature Identifier.
     * 
     * @author Jody Garnett, Refractions Research Inc.
     */
    private static class FidSimpleFeaturePropertyAccessor implements PropertyAccessor {

        @Override
        public boolean canHandle(Object object, String xpath, Class target) {
            //we only work against feature, not feature type
            return object instanceof Feature && xpath.matches("@(\\w+:)?id");
        }

        @Override
        public Object get(Object object, String xpath, Class target) {
            final Feature feature = (Feature) object;
            return feature.getIdentifier().getID();
        }

        @Override
        public void set(Object object, String xpath, Object value, Class target)
                throws IllegalArgumentException {
            throw new IllegalArgumentException("feature id is immutable");
        }
    }

    static class DefaultGeometrySimpleFeaturePropertyAccessor implements PropertyAccessor {

        @Override
        public boolean canHandle(Object object, String xpath, Class target) {
            if (!"".equals(xpath)) {
                return false;
            }

//        	if ( target != Geometry.class ) 
//        		return false;

            if (!(object instanceof Feature || object instanceof FeatureType)) {
                return false;
            }

            return true;

        }

        @Override
        public Object get(Object object, String xpath, Class target) {
            if(object instanceof SimpleFeature){
                return ((SimpleFeature) object).getDefaultGeometry();
            }else if (object instanceof Feature) {
                return ((Feature) object).getDefaultGeometryProperty().getValue();
            }
            if (object instanceof FeatureType) {
                return ((FeatureType) object).getGeometryDescriptor();
            }

            return null;
        }

        @Override
        public void set(Object object, String xpath, Object value, Class target)
                throws IllegalArgumentException{

            if (object instanceof Feature) {
                ((Feature) object).getDefaultGeometryProperty().setValue(value);
            }
            if (object instanceof FeatureType) {
                throw new IllegalArgumentException("feature type is immutable");
            }

        }
    }

    static class SimpleFeaturePropertyAccessor implements PropertyAccessor {

        @Override
        public boolean canHandle(Object object, String xpath, Class target) {
            xpath = stripPrefix(xpath);

            if (object instanceof Feature) {
                return ((Feature) object).getProperty(xpath) != null;
            }

            if (object instanceof FeatureType) {
                return ((FeatureType) object).getDescriptor(xpath) != null;
            }

            return false;
        }

        @Override
        public Object get(Object object, String xpath, Class target) {
            xpath = stripPrefix(xpath);

            if(object instanceof SimpleFeature){
                ((SimpleFeature) object).getAttribute(xpath);
            }

            if (object instanceof Feature) {
                return ((Feature) object).getProperty(xpath).getValue();
            }

            if (object instanceof FeatureType) {
                return ((FeatureType) object).getDescriptor(xpath);
            }

            return null;
        }

        @Override
        public void set(Object object, String xpath, Object value, Class target)
                throws IllegalArgumentException {
            xpath = stripPrefix(xpath);

            if(object instanceof SimpleFeature){
                ((SimpleFeature) object).setAttribute(xpath, value);
            }

            if (object instanceof Feature) {
                ((Feature) object).getProperty(xpath).setValue(value);
            }

            if (object instanceof FeatureType) {
                throw new IllegalArgumentException("Feature type is immutable");
            }

        }
    }
}

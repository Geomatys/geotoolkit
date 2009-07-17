/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Set;
import java.util.TreeSet;

import org.geotoolkit.feature.SimpleIllegalAttributeException;
import org.geotoolkit.util.Converters;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Attribute;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;


/**
 * This is a set of utility methods used when <b>implementing</b> types.
 * <p>
 * This set of classes captures the all important how does it work questions,
 * particularly with respect to super types.
 * </p>
 * FIXME: These methods need a Q&A check to confirm correct use of Super TODO:
 * Cannot tell the difference in intent from FeatureTypes
 *
 * @author Jody Garnett, LISAsoft
 * @author Justin Deoliveira, The Open Planning Project
 */
public class FeatureValidationUtilities {

    private FeatureValidationUtilities(){}

    /**
     * Ensures an attribute value is withing the restrictions of the AttributeDescriptor and
     * AttributeType.
     * @param attribute
     * @return true if the attribute value is valid
     */
    public static boolean isValid(final Attribute attribute) {

        try {
            validate(attribute.getType(), attribute, attribute.getValue(), false);
            return true;
        } catch (IllegalAttributeException invalid) {
            return false;
        }
    }

    /**
     * Validates content against an attribute.
     *
     * @param attribute
     *            The attribute.
     * @param attributeContent
     *            Content of attribute (often attribute.getValue()
     *
     * @throws IllegalAttributeException
     *             In the event that content violates any restrictions specified
     *             by the attribute.
     */
    public static void validate(final Attribute attribute, final Object attributeContent)
            throws IllegalAttributeException {
        validate(attribute.getType(), attribute, attributeContent, false);
    }

    /**
     *
     * @param type AttributeType (often attribute.getType() )
     * @param attribute Attribute being tested
     * @param attributeContent Content of the attribute (often attribute.getValue() )
     * @throws IllegalAttributeException
     */
    public static void validate(final AttributeType type, final Attribute attribute,
            final Object attributeContent) throws IllegalAttributeException{
        validate(type, attribute, attributeContent, false);
    }

    /**
     *
     * @param type AttributeType (often attribute.getType() )
     * @param attribute Attribute being tested
     * @param attributeContent Content of the attribute (often attribute.getValue() )
     * @param isSuper True if super type is being checked
     * @throws IllegalAttributeException
     */
    protected static void validate(final AttributeType type, final Attribute attribute,
            final Object attributeContent, final boolean isSuper) throws IllegalAttributeException{

        if (type == null) {
            throw new SimpleIllegalAttributeException("null type");
        }

        if (attributeContent == null) {
            if (!attribute.isNillable()) {
                throw new SimpleIllegalAttributeException(type.getName() + " not nillable");
            }
            return;
        }

        if (type.isIdentified() && attribute.getIdentifier() == null) {
            throw new NullPointerException(type.getName() + " is identified, null id not accepted");
        }

        if (!isSuper) {

            // JD: This is an issue with how the xml simpel type hierarchy
            // maps to our current Java Type hiearchy, the two are inconsitent.
            // For instance, xs:integer, and xs:int, the later extend the
            // former, but their associated java bindings, (BigDecimal, and
            // Integer)
            // dont.
            final Class clazz = attributeContent.getClass();
            final Class binding = type.getBinding();
            if (binding != null && binding != clazz && !binding.isAssignableFrom(clazz)) {
                throw new SimpleIllegalAttributeException(clazz.getName() + " is not an acceptable class for " + type.getName() + " as it is not assignable from " + binding);
            }
        }

        if (type.getRestrictions() != null) {
            for (Filter f : type.getRestrictions()) {
                if (!f.evaluate(attribute)) {
                    throw new SimpleIllegalAttributeException("Attribute instance (" + attribute.getIdentifier() + ")" + "fails to pass filter: " + f);
                }
            }
        }

        // move up the chain,
        if (type.getSuper() != null) {
            validate(type.getSuper(), attribute, attributeContent, true);
        }
    }

    /**
     * Ensure that attributeContent is a good value for descriptor.
     */
    public static void validate(final AttributeDescriptor descriptor, final Object value) throws IllegalAttributeException {

        if (descriptor == null) {
            throw new NullPointerException("Attribute descriptor required for validation");
        }

        if (value == null) {
            if (!descriptor.isNillable()) {
                throw new IllegalArgumentException(descriptor.getName() + " requires a non null value");
            }
        } else {
            validate(descriptor.getType(), value, false);
        }
    }

    /**
     * Do our best to make the provided value line up with the needs of descriptor.
     * <p>
     * This helper method uses the Coverters api to convert the provided
     * value into the required class. If the value is null (and the attribute
     * is not nillable) a default value will be returned.
     * @param descriptor Attribute descriptor we need to supply a value for.
     * @param value The provided value
     * @return Our best attempt to make a valid value
     * @throws IllegalArgumentException if we really could not do it.
     */
    public static Object parse(final AttributeDescriptor descriptor, final Object value) throws IllegalArgumentException {
        if (value == null) {
            if (descriptor.isNillable()) {
                return descriptor.getDefaultValue();
            }
        } else {
            final Class target = descriptor.getType().getBinding();
            if (!target.isAssignableFrom(value.getClass())) {
                // attempt to convert
                Object converted = Converters.convert(value, target);
                if (converted != null) {
                    return converted;
                }
//                else {
//                    throw new IllegalArgumentException( descriptor.getLocalName()+ " could not convert "+value+" into "+target);
//                }
            }
        }
        return value;
    }

    protected static void validate(final AttributeType type, final Object value, boolean isSuper)
            throws IllegalAttributeException{
        if (!isSuper) {
            // JD: This is an issue with how the xml simpel type hierarchy
            // maps to our current Java Type hiearchy, the two are inconsitent.
            // For instance, xs:integer, and xs:int, the later extend the
            // former, but their associated java bindings, (BigDecimal, and
            // Integer)
            // dont.
            final Class clazz = value.getClass();
            final Class binding = type.getBinding();

            if (binding != null && !binding.isAssignableFrom(clazz)) {
                throw new SimpleIllegalAttributeException(clazz.getName() + " is not an acceptable class for " + type.getName() + " as it is not assignable from " + binding);
            }
        }

        if (type.getRestrictions() != null && type.getRestrictions().size() > 0) {
            for (Filter filter : type.getRestrictions()) {
                if (!filter.evaluate(value)) {
                    throw new SimpleIllegalAttributeException(type.getName() + " restriction " + filter + " not met by: " + value);
                }
            }
        }

        // move up the chain,
        if (type.getSuper() != null) {
            validate(type.getSuper(), value, true);
        }
    }

    /**
     * FeatureType comparison indicating if the description provided by two FeatureTypes is
     * similar to the point data can be exchanged. This comparison is really very focused on the
     * name / value contract and is willing to overlook details like length restrictions.
     * <p>
     * When creating compatible FeatureTypes you will find some systems have different abilities
     * which is reflected in how well they support a given FeatureType.
     * <p>
     * As an example databases traditionally support variable length strings with a
     * limit of 32 k; while a shapefile is limited to 256 characters. When working with data from
     * both these data sources you will need to make adjustments based on these abilities.
     * </p>
     * If true is returned data conforming to the expected FeatureType can be used with the
     * actual FeatureType.
     * <p>
     * After assertOrderCovered returns without error the following code will work:<pre><code>
     * for( Property property : feature.getProperties() ){
     *     Object value = property.getValue();
     *
     *     Property target = newFeature.getProperty( property.getName().getLocalPart() );
     *     target.setValue( value );
     * }
     * </code></pre>
     * Specifically this says that between the two feature types data is assignable on a name by name
     * basis.
     *
     * @param expected Expected FeatureType being used to compare against
     * @param actual Actual FeatureType
     * @return true if actual is equal to or a subset of the expected feature type.
     */
    public static void assertNameAssignable(final FeatureType expected, final FeatureType actual) {
        // check feature type name
        String expectedName = expected.getName().getLocalPart();
        final String actualName = actual.getName().getLocalPart();
        if (!expectedName.equals(actualName)) {
            throw new SimpleIllegalAttributeException("Expected '" + expectedName + "' but was supplied '" + actualName + "'.");
        }
        // check attributes names
        final Set<String> names = new TreeSet<String>();
        for (PropertyDescriptor descriptor : actual.getDescriptors()) {
            names.add(descriptor.getName().getLocalPart());
        }
        for (PropertyDescriptor descriptor : expected.getDescriptors()) {
            expectedName = descriptor.getName().getLocalPart();
            if (names.contains(expectedName)) {
                names.remove(expectedName); // only use once!
            } else {
                throw new SimpleIllegalAttributeException("Expected to find a match for '" + expectedName + "' but was not available remaining names: " + names);
            }
        }
        if (!names.isEmpty()) {
            throw new SimpleIllegalAttributeException("Expected to find attributes '" + expectedName + "' but was not available remaining names: " + names);
        }

        // check attribute bindings
        for (PropertyDescriptor expectedDescriptor : expected.getDescriptors()) {
            expectedName = expectedDescriptor.getName().getLocalPart();
            final PropertyDescriptor actualDescriptor = actual.getDescriptor(expectedName);

            final Class<?> expectedBinding = expectedDescriptor.getType().getBinding();
            final Class<?> actualBinding = actualDescriptor.getType().getBinding();
            if (!actualBinding.isAssignableFrom(expectedBinding)) {
                throw new IllegalArgumentException("Expected " + expectedBinding.getSimpleName() + " for " + expectedName + " but was " + actualBinding.getSimpleName());
            }
        }
    }

    /**
     * SimpleFeatureType comparison indicating that data from one FeatureType can
     * be exchanged with another - specifically ensuring that the order / value is a
     * reasonable match with the expected number of attributes on each side and the
     * values correctly assignable.
     * <p>
     * After assertOrderCovered returns without error the following code will work:<pre><code>
     * List<Object> values = feature.getAttributes();
     * newFeature.setAttributes( values );
     * </code></pre>
     *
     * @param expected
     * @param actual
     * @return
     */
    public static void assertOrderAssignable(final SimpleFeatureType expected, final SimpleFeatureType actual) {
        // check feature type name
        final String expectedName = expected.getName().getLocalPart();
        final String actualName = actual.getName().getLocalPart();
        if (!expectedName.equals(actualName)) {
            throw new SimpleIllegalAttributeException("Expected '" + expectedName + "' but was supplied '" + actualName + "'.");
        }
        // check attributes names
        if (expected.getAttributeCount() != actual.getAttributeCount()) {
            throw new SimpleIllegalAttributeException("Expected " + expected.getAttributeCount() + " attributes, but was supplied " + actual.getAttributeCount());
        }
        for (int i = 0; i < expected.getAttributeCount(); i++) {
            Class<?> expectedBinding = expected.getDescriptor(i).getType().getBinding();
            Class<?> actualBinding = actual.getDescriptor(i).getType().getBinding();
            if (!actualBinding.isAssignableFrom(expectedBinding)) {
                String name = expected.getDescriptor(i).getLocalName();
                throw new IllegalArgumentException("Expected " + expectedBinding.getSimpleName() + " for " + name + " but was " + actualBinding.getSimpleName());
            }
        }
    }
    
}
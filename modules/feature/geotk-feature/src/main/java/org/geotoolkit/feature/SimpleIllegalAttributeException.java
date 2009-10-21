/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

/**
 * Indicates client class has attempted to create an invalid feature.
 * @source $URL$
 * @module pending
 */
public class SimpleIllegalAttributeException extends IllegalAttributeException {

    /**
     * A descriptor for an attribute that does not exist.
     */
    private static final AttributeDescriptor NULL_ATTRIBUTE_DESCRIPTOR = new AttributeDescriptor(){

        @Override
        public int getMaxOccurs() {
            return 0;
        }

        @Override
        public int getMinOccurs() {
            return 0;
        }

        @Override
        public Name getName() {
            return null;
        }

        @Override
        public Map<Object, Object> getUserData() {
            return null;
        }

        @Override
        public boolean isNillable() {
            return false;
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public AttributeType getType() {
            return null;
        }

    };

    /**
     * Constructor with message argument.
     *
     * @param message Reason for the exception being thrown
     */
    public SimpleIllegalAttributeException(String message) {
        super(NULL_ATTRIBUTE_DESCRIPTOR,null,message);
    }

    /**
     * Constructor that makes the message given the expected and invalid.
     *
     * @param expected the expected AttributeType.
     * @param invalid the attribute that does not validate against expected.
     */
    public SimpleIllegalAttributeException(AttributeDescriptor expected, Object invalid) {
        super(expected, invalid );
    }

    /**
     * Constructor that makes the message given the expected and invalid, along
     * with the root cause.
     *
     * @param expected the expected AttributeType.
     * @param invalid the attribute that does not validate against expected.
     * @param cause the root cause of the error.
     */
    public SimpleIllegalAttributeException(AttributeDescriptor expected, Object invalid, Throwable cause) {
        super( expected, invalid, cause );
    }
    
}

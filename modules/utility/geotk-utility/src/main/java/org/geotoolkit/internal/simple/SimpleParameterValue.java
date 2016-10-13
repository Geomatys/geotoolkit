/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.internal.simple;

import java.net.URI;
import java.io.Serializable;
import javax.measure.Unit;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.InvalidParameterTypeException;
import org.opengis.parameter.InvalidParameterValueException;
import org.geotoolkit.util.Cloneable;


/**
 * A trivial implementation of {@link ParameterValue}.
 *
 * @param <T> The type of value.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public class SimpleParameterValue<T> implements ParameterValue<T>, Cloneable, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8445712722603023516L;

    /**
     * The parameter descriptor given in argument to the constructor.
     */
    protected final ParameterDescriptor<T> descriptor;

    /**
     * The value returned by {@link #getValue()} or modified by {@link #setValue(Object)}.
     */
    protected T value;

    /**
     * Creates a new parameter value.
     *
     * @param descriptor The descriptor associated to this parameter value.
     */
    public SimpleParameterValue(final ParameterDescriptor<T> descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Sets the value to the given object.
     *
     * @param  value The new value, or {@code null}.
     * @throws IllegalArgumentException If the value can not be set to the given argument.
     */
    @Override
    public void setValue(final Object value) throws IllegalArgumentException {
        try {
            this.value = descriptor.getValueClass().cast(value);
        } catch (ClassCastException cause) {
            IllegalArgumentException e = invalidValue(value);
            e.initCause(cause);
            throw e;
        }
    }

    @Override public T        getValue()                              {return value;}
    @Override public Unit<?>  getUnit()                               {return null;}
    @Override public double   doubleValue(Unit<?> unit)               {throw invalidType();}
    @Override public double   doubleValue()                           {throw invalidType();}
    @Override public int      intValue()                              {throw invalidType();}
    @Override public boolean  booleanValue()                          {throw invalidType();}
    @Override public String   stringValue()                           {throw invalidType();}
    @Override public double[] doubleValueList(Unit<?> unit)           {throw invalidType();}
    @Override public double[] doubleValueList()                       {throw invalidType();}
    @Override public int[]    intValueList()                          {throw invalidType();}
    @Override public URI      valueFile()                             {throw invalidType();}
    @Override public void     setValue(double   value, Unit<?> unit)  {throw invalidValue(value);}
    @Override public void     setValue(double   value)                {throw invalidValue(value);}
    @Override public void     setValue(int      value)                {throw invalidValue(value);}
    @Override public void     setValue(boolean  value)                {throw invalidValue(value);}
    @Override public void     setValue(double[] values, Unit<?> unit) {throw invalidValue(values);}
    @Override public          ParameterDescriptor<T> getDescriptor()  {return descriptor;}

    /**
     * Returns the parameter name, for formatting exception only.
     */
    private String getParameterName() {
        return descriptor.getName().getCode();
    }

    /**
     * Invoked when any getter method other than {@link #getValue()} has been invoked.
     */
    private IllegalStateException invalidType() {
        return new InvalidParameterTypeException(null, getParameterName());
    }

    /**
     * Invoked when any setter method other than {@link #setValue(Object)} has been invoked.
     */
    private IllegalArgumentException invalidValue(final Object value) {
        throw new InvalidParameterValueException(null, getParameterName(), value);
    }

    /**
     * Returns a clone of this parameter value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ParameterValue<T> clone() {
        try {
            return (ParameterValue<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns a string representation of this parameter value. Current implementation
     * does not format the {@linkplain #value} because it may be a complex object.
     */
    @Override
    public String toString() {
        return "ParameterValue<" + descriptor.getValueClass().getSimpleName() + ">[\"" + getParameterName() + "\"]";
    }
}

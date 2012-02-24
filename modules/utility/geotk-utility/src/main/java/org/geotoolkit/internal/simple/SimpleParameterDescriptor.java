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

import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import javax.measure.unit.Unit;

import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.referencing.ReferenceIdentifier;


/**
 * A trivial implementation of {@link ParameterDescriptor}. This is defined as a subtype of
 * {@link SimpleReferenceIdentifier} and {@link SimpleCitation} only as an opportunist way
 * (not something generally recommended).
 *
 * @param <T> The type of value. This type is returned by {@link #getValueClass()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public class SimpleParameterDescriptor<T> extends SimpleReferenceIdentifier implements ParameterDescriptor<T> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8019956359406548456L;

    /**
     * The value type to be returned by {@link #getValueClass()}.
     */
    protected final Class<T> type;

    /**
     * Creates a new identifier for the given code space and code value.
     * The given code space is also used for constructing a simple authority.
     *
     * @param type      The value type to be returned by {@link #getValueClass()}.
     * @param codespace The string to be returned by {@link #getCodeSpace()}.
     * @param code      The string to be returned by {@link #getCode()}.
     */
    public SimpleParameterDescriptor(final Class<T> type, final String codespace, final String code) {
        super(codespace, code);
        this.type = type;
    }

    @Override public ReferenceIdentifier      getName()          {return this;}
    @Override public Collection<GenericName>  getAlias()         {return Collections.emptySet();}
    @Override public Set<ReferenceIdentifier> getIdentifiers()   {return Collections.emptySet();}
    @Override public Class<T>                 getValueClass()    {return type;}
    @Override public Set<T>                   getValidValues()   {return null;}
    @Override public T                        getDefaultValue()  {return null;}
    @Override public Comparable<T>            getMinimumValue()  {return null;}
    @Override public Comparable<T>            getMaximumValue()  {return null;}
    @Override public Unit<?>                  getUnit()          {return null;}
    @Override public int                      getMinimumOccurs() {return 0;}
    @Override public int                      getMaximumOccurs() {return 1;}
    @Override public InternationalString      getRemarks()       {return null;}
    @Override public String                   toWKT()            {throw new UnsupportedOperationException();}
    @Override public ParameterValue<T>        createValue()      {return new SimpleParameterValue<>(this);}

    /**
     * Returns a string representation of this descriptor.
     */
    @Override
    public String toString() {
        return "ParameterDescriptor<" + type.getSimpleName() + ">[\"" + title + ':' + code + "\"]";
    }
}

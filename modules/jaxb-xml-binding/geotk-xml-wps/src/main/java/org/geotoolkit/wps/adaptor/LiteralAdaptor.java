/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.adaptor;

import javax.measure.Unit;
import javax.measure.format.ParserException;
import org.apache.sis.measure.Units;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.ows.xml.DomainMetadata;
import org.geotoolkit.ows.xml.v200.DomainMetadataType;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.xml.v100.InputType;
import org.geotoolkit.wps.xml.v100.LiteralOutputType;
import org.geotoolkit.wps.xml.v100.OutputDataType;
import org.geotoolkit.wps.xml.v100.SupportedUOMsType;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.DataOutputType;
import org.geotoolkit.wps.xml.v200.LiteralDataType;
import org.geotoolkit.wps.xml.v200.LiteralValue;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class LiteralAdaptor<T> implements DataAdaptor<T> {

    private final ObjectConverter<String,T> converter;
    private final Unit unit;
    private final String version;

    private LiteralAdaptor(ObjectConverter<String,T> converter, Unit unit, String version){
        this.converter = converter;
        this.unit = unit;
        this.version = version;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getVersion() {
        return version;
    }

    public Class<T> getValueClass() {
        return converter.getTargetClass();
    }

    public T convert(String value) {
        return converter.apply(value);
    }

    @Override
    public InputType toWPS1Input(Object candidate) {
        final String uom = unit != null ? unit.toString() : null;

        return InputType.createLiteral(
                "",
                String.valueOf(candidate),
                WPSConvertersUtils.getDataTypeString(version, converter.getTargetClass()),
                uom);
    }

    public DataInputType toWPS2Input(Object candidate, boolean rawLiteral) {

        final Data data = new Data();
        final LiteralValue litValue = new LiteralValue();
        litValue.setDataType(WPSConvertersUtils.getDataTypeString("2.0.0", getValueClass()));
        litValue.setValue(String.valueOf(candidate));
        litValue.setUom(unit==null ? null : unit.getName());

        if (rawLiteral) {
            data.getContent().add(String.valueOf(candidate));
        } else {
            data.getContent().add(litValue);
        }

        final DataInputType dit = new DataInputType();
        dit.setData(data);


        return dit;
    }

    @Override
    public DataInputType toWPS2Input(Object candidate) {
        return toWPS2Input(candidate, false);
    }

    @Override
    public T fromWPS1Input(OutputDataType candidate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T fromWPS2Input(DataOutputType candidate) {
        final Data data = candidate.getData();
        final LiteralValue literalData = data.getLiteralData();
        final String value = literalData.getValue();
        return convert(value);
    }

    public static LiteralAdaptor create(LiteralDataType.LiteralDataDomain domain) {

        Class clazz = getValueClass(domain.getDataType());
        if (clazz == null) clazz = String.class;

        final Unit unit = getUnit(domain.getUOM());

        final ObjectConverter cvt = ObjectConverters.find(String.class, clazz);
        return new LiteralAdaptor(cvt,unit,"2.0.0");
    }

    public static LiteralAdaptor create(LiteralOutputType domain) {

        Class clazz = getValueClass(domain.getDataType());
        if (clazz == null) clazz = String.class;

        Unit unit = null;
        final SupportedUOMsType uoMs = domain.getUOMs();
        if (uoMs!=null) {
            final SupportedUOMsType.Default def = uoMs.getDefault();
            if (def!=null) {
                try {
                    unit = Units.valueOf(def.getUOM().getValue());
                } catch (ParserException ex) {
                    unit = Units.valueOf(def.getUOM().getReference());
                }
            }
            if (uoMs.getSupported()!=null) {
                for (org.geotoolkit.ows.xml.v110.DomainMetadataType t : uoMs.getSupported().getUOM()) {
                    if (unit!=null) break;
                    try {
                        unit = Units.valueOf(t.getValue());
                    } catch (ParserException ex) {
                        unit = Units.valueOf(t.getReference());
                    }
                }
            }
        }

        final ObjectConverter cvt = ObjectConverters.find(String.class, clazz);
        return new LiteralAdaptor(cvt,unit,"1.0.0");
    }

    private static Class getValueClass(DomainMetadata type) {
        if(type==null) return null;
        Class clazz = findClass(type.getReference());
        if(clazz==null) clazz = findClass(type.getValue());
        if(clazz==null) clazz = String.class;
        return clazz;
    }

    private static Class findClass(String value) {
        if(value==null) return null;
        Class clazz = null;
        try {
            clazz = Class.forName(value);
        } catch (ClassNotFoundException ex) {
            value = value.toLowerCase();
            if (value.contains("double")) {
                clazz = Double.class;
            } else if (value.contains("boolean")) {
                clazz = Boolean.class;
            } else if (value.contains("float")) {
                clazz = Float.class;
            } else if (value.contains("short")) {
                clazz = Short.class;
            } else if (value.contains("integer")) {
                clazz = Integer.class;
            } else if (value.contains("long")) {
                clazz = Long.class;
            }
        }
        return clazz;
    }

    private static Unit getUnit(DomainMetadataType type) {
        if(type==null || type.getValue()==null) return null;
        return Units.valueOf(type.getValue());
    }

}

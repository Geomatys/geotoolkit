/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.wps.json;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ows.xml.v200.RangeType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LiteralDataDomain {

    private AllowedValues allowedValues = null;

    private AllowedRanges allowedRanges = null;

    private Boolean anyValue = null;

    private String valuesReference;

    private String defaultValue = null;

    private NameReferenceType dataType;

    private NameReferenceType uom;

    public LiteralDataDomain() {

    }

    public LiteralDataDomain(org.geotoolkit.wps.xml.v200.LiteralDataDomain lit) {
        if (lit != null) {
            if (lit.getAllowedValues() != null &&  lit.getAllowedValues().getStringValues() != null && !lit.getAllowedValues().getStringValues().isEmpty()) {
                this.allowedValues = new AllowedValues(lit.getAllowedValues().getStringValues());
            }
            if (lit.getAllowedRanges()!= null &&  lit.getAllowedRanges().getRangeValues()!= null && !lit.getAllowedRanges().getRangeValues().isEmpty()) {
                List<Range> newRanges = new ArrayList<>();
                for (RangeType range : lit.getAllowedRanges().getRangeValues()) {
                    newRanges.add(new Range(range));
                }
                this.allowedRanges = new AllowedRanges(newRanges);
            }
            if (lit.getAnyValue() != null) {
                this.anyValue = true;
            }
            if (lit.getDefaultValue()!= null) {
                this.defaultValue = lit.getDefaultValue().getValue();
            }
            if (lit.getDataType()!= null) {
                this.dataType = new NameReferenceType(lit.getDataType().getValue(), lit.getDataType().getReference());
            }
            if (lit.getUOM()!= null) {
                this.uom = new NameReferenceType(lit.getUOM().getValue(), lit.getUOM().getReference());
            }
            if (lit.getValuesReference() != null) {
                if (lit.getValuesReference().getValue() != null) {
                    this.valuesReference = lit.getValuesReference().getValue();
                } else if (lit.getValuesReference().getReference()!= null) {
                    this.valuesReference = lit.getValuesReference().getReference();
                }
            }
        }

    }

    public LiteralDataDomain(LiteralDataDomain that) {
        if (that != null) {
            if (that.allowedValues != null && that.allowedValues.getAllowedValues() != null && !that.allowedValues.getAllowedValues().isEmpty()) {
                this.allowedValues = new AllowedValues(that.allowedValues);
            }
            if (that.allowedRanges != null && that.allowedRanges.getAllowedRanges()!= null && !that.allowedRanges.getAllowedRanges().isEmpty()) {
                this.allowedRanges = new AllowedRanges(that.allowedRanges.getAllowedRanges());
            }
            this.anyValue = that.anyValue;
            this.valuesReference = that.valuesReference;
            this.defaultValue = that.defaultValue;
            if (that.dataType != null) {
                this.dataType = new NameReferenceType(that.dataType);
            }
            if (that.uom != null) {
                this.uom = new NameReferenceType(that.uom);
            }
        }

    }

    public LiteralDataDomain(NameReferenceType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dataType
     */
    public NameReferenceType getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(NameReferenceType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the allowedValues
     */
    public AllowedValues getAllowedValues() {
        return allowedValues;
    }

    /**
     * @param allowedValues the allowedValues to set
     */
    public void setAllowedValues(AllowedValues allowedValues) {
        this.allowedValues = allowedValues;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the valuesReference
     */
    public String getValuesReference() {
        return valuesReference;
    }

    /**
     * @param valuesReference the valuesReference to set
     */
    public void setValuesReference(String valuesReference) {
        this.valuesReference = valuesReference;
    }

    /**
     * @return the allowedRanges
     */
    public AllowedRanges getAllowedRanges() {
        return allowedRanges;
    }

    /**
     * @param allowedRanges the allowedRanges to set
     */
    public void setAllowedRanges(AllowedRanges allowedRanges) {
        this.allowedRanges = allowedRanges;
    }

    /**
     * @return the anyValue
     */
    public Boolean getAnyValue() {
        return anyValue;
    }

    /**
     * @param anyValue the anyValue to set
     */
    public void setAnyValue(Boolean anyValue) {
        this.anyValue = anyValue;
    }

    /**
     * @return the uom
     */
    public NameReferenceType getUom() {
        return uom;
    }

    /**
     * @param uom the uom to set
     */
    public void setUom(NameReferenceType uom) {
        this.uom = uom;
    }

}

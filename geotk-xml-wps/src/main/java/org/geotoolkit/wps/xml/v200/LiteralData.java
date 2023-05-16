/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.v200.AllowedValues;
import org.geotoolkit.ows.xml.v200.AnyValue;
import org.geotoolkit.ows.xml.v200.DomainMetadataType;
import org.geotoolkit.ows.xml.v200.ValuesReference;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.SupportedUOMs;
import org.geotoolkit.wps.xml.v100.LegacyValuesReference;


/**
 * <p>Java class for LiteralData complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LiteralData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DataDescription">
 *       &lt;sequence>
 *         &lt;element name="LiteralDataDomain" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/wps/2.0}LiteralDataDomain">
 *                 &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "LiteralDataType", propOrder = {
    "domainToMarshal",
    "dataType",
    "UOMs",
    "allowedValues",
    "anyValue",
    "valuesReference",
    "defaultValue"
})
@XmlRootElement(name = "LiteralData")
public class LiteralData extends DataDescription {

    private boolean isParentOutput = false;

    protected List<LiteralDataDomain> literalDataDomain;

    public LiteralData() {}

    public LiteralData(List<Format> formats, AllowedValues allowedValues, AnyValue anyValue, ValuesReference valuesReference, DomainMetadataType dataType, DomainMetadataType uom,
                String defaultValue, Boolean _default) {
        super(formats);
        literalDataDomain = new ArrayList<>();
        literalDataDomain.add(new LiteralDataDomain(allowedValues, anyValue, valuesReference, dataType, uom, defaultValue, _default));
    }

    public LiteralData(List<Format> formats, DomainMetadataType dataType, DomainMetadataType uom, AnyValue anyValue) {
        super(formats);
        literalDataDomain = new ArrayList<>();
        literalDataDomain.add(new LiteralDataDomain(dataType, uom, anyValue));
    }

    public LiteralData(List<Format> formats, List<LiteralDataDomain> lits) {
        super(formats);
        literalDataDomain = lits;
    }

    /**
     * Gets the value of the literalDataDomain property.
     *
     */
    public List<LiteralDataDomain> getLiteralDataDomain() {
        if (literalDataDomain == null) {
            literalDataDomain = new ArrayList<>();
        }
        return this.literalDataDomain;
    }

    /**
     * @implNote : to be conform with the standard, we force the empty namespace,
     * but I cannot understand why it's not part of wps namespace.
     * Also, we use a private method for marshalling, as we return a null value
     * when marshalling WPS 1.
     *
     * @return The list of domains to marshal.
     */
    @XmlElement(name = "LiteralDataDomain", namespace="", required = true)
    private List<LiteralDataDomain> getDomainToMarshal() {
        if (FilterByVersion.isV1())
            return null;
        if (literalDataDomain == null) {
            literalDataDomain = new ArrayList<>();
        }
        return this.literalDataDomain;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (literalDataDomain != null) {
            sb.append("literalDataDomain:\n");
            for (LiteralDataDomain out : literalDataDomain) {
                sb.append(out).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LiteralData && super.equals(object)) {
            final LiteralData that = (LiteralData) object;
            return Objects.equals(this.literalDataDomain, that.literalDataDomain);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.literalDataDomain);
        return hash;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    private LiteralDataDomain cachedDefault;
    private SupportedUOMs uoMs;

    private Optional<LiteralDataDomain> getDefaultOrFirstDomain() {
        if (cachedDefault == null) {
            cachedDefault = getLiteralDataDomain().stream()
                .sorted((d1, d2) -> {
                    final boolean d1IsDefault = Boolean.TRUE.equals(d1.isDefault());
                    final boolean d2IsDefault = Boolean.TRUE.equals(d1.isDefault());
                    if (d1IsDefault && d2IsDefault) {
                        return 0;
                    } else if (d1IsDefault) {
                        return -1;
                    } else if (d2.isDefault()) {
                        return 1;
                    }
                    return 0;
                })
                .findFirst()
                .orElse(null);
        }

        return Optional.ofNullable(cachedDefault);
    }

    private LiteralDataDomain createDefaultDomain() {
        cachedDefault = new LiteralDataDomain();
        cachedDefault.setDefault(Boolean.TRUE);
        getLiteralDataDomain().add(0, cachedDefault);
        return cachedDefault;
    }

    /**
     * Indicates that there are a finite set of values and ranges allowed for this input, and contains list of all the valid values and/or ranges of values.
     * Notice that these values and ranges can be displayed to a human client.
     *
     * @return
     *     possible object is
     *     {@link AllowedValues }
     *
     */
    @XmlElement(name = "AllowedValues", namespace=WPSMarshallerPool.OWS_2_0_NAMESPACE)
    private AllowedValues getAllowedValues() {
        if (FilterByVersion.isV2())
            return null;
        return getDefaultOrFirstDomain()
                .map(LiteralDataDomain::getAllowedValues)
                .orElse(null);
    }

    /**
     * Indicates that there are a finite set of values and ranges allowed for this input, and contains list of all the valid values and/or ranges of values.
     * Notice that these values and ranges can be displayed to a human client.
     *
     * @param value
     *     allowed object is
     *     {@link AllowedValues }
     *
     */
    private void setAllowedValues(final AllowedValues value) {
        if (value == null)
            return;
        getDefaultOrFirstDomain()
                .orElseGet(this::createDefaultDomain)
                .setAllowedValues(value);
    }

    /**
     * Indicates that any value is allowed for this input. This element shall be included when there are no restrictions,
     * except for data type, on the allowable value of this input.
     *
     * @return
     *     possible object is
     *     {@link AnyValue }
     *
     */
    @XmlElement(name = "AnyValue", namespace=WPSMarshallerPool.OWS_2_0_NAMESPACE)
    private AnyValue getAnyValue() {
        if (FilterByVersion.isV2() || isParentOutput)
            return null;
        return getDefaultOrFirstDomain()
                .map(LiteralDataDomain::getAnyValue)
                .orElse(null);
    }

    /**
     * Indicates that any value is allowed for this input. This element shall be included when there are no restrictions,
     * except for data type, on the allowable value of this input.
     *
     * @param value
     *     allowed object is
     *     {@link AnyValue }
     *
     */
    private void setAnyValue(final AnyValue value) {
        if (value == null)
            return;
        getDefaultOrFirstDomain()
                .orElseGet(this::createDefaultDomain)
                .setAnyValue(value);
    }

    /**
     * Gets the value of the valuesReference property.
     *
     * @return
     *     possible object is
     *     {@link LegacyValuesReference }
     *
     */
    @XmlElement(name = "ValuesReference")
    private LegacyValuesReference getValuesReference() {
        if (FilterByVersion.isV2())
            return null;
        return getDefaultOrFirstDomain()
                .map(LiteralDataDomain::getValuesReference)
                .map(ows2ValueRef -> new LegacyValuesReference(ows2ValueRef.getValue(), ows2ValueRef.getReference()))
                .orElse(null);
    }

    /**
     * Sets the value of the valuesReference property.
     *
     * @param value
     *     allowed object is
     *     {@link LegacyValuesReference }
     *
     */
    private void setValuesReference(final LegacyValuesReference value) {
        if (value != null) {
            final ValuesReference ows2Ref = new ValuesReference(value.getValue(), value.getReference());
            getDefaultOrFirstDomain()
                .orElseGet(this::createDefaultDomain)
                .setValuesReference(ows2Ref);
        }
    }

    /**
     * Gets the value of the defaultValue property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @XmlElement(name = "DefaultValue")
    private String getDefaultValue() {
        if (FilterByVersion.isV2())
            return null;
        return getDefaultOrFirstDomain()
                .map(LiteralDataDomain::getDefaultValue)
                .map(valueType -> valueType.getValue())
                .orElse(null);
    }

    /**
     * Sets the value of the defaultValue property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    private void setDefaultValue(final String value) {
        if (value == null)
            return;

        LiteralDataDomain domain = getDefaultOrFirstDomain()
                .orElseGet(this::createDefaultDomain);
        final String dataType = domain.getDataType() == null ? null : domain.getDataType().getValue();
        final String uom = domain.getUOM() == null ? null : domain.getUOM().getValue();
        domain.setDefaultValue(new LiteralValue(value, dataType, uom));
    }

    @XmlElement(name = "DataType", namespace=WPSMarshallerPool.OWS_2_0_NAMESPACE)
    public DomainMetadataType getDataType() {
        if (FilterByVersion.isV2())
            return null;
        return getDefaultOrFirstDomain()
                .map(LiteralDataDomain::getDataType)
                .orElse(null);
    }

    public void setDataType(final DomainMetadataType meta) {
        if (meta == null)
            return;
        getDefaultOrFirstDomain()
                .orElseGet(this::createDefaultDomain)
                .setDataType(meta);
    }

    /**
     * Gets the value of the uoMs property.
     *
     * @return
     *     possible object is
     *     {@link SupportedUOMs }
     * @deprecated WPS 1.0 retro-compatibility
     */
    @XmlElement(name = "UOMs")
    @Deprecated
    @XmlJavaTypeAdapter(FilterV1.SupportedUOMs.class)
    public SupportedUOMs getUOMs() {
        if (uoMs == null) {
            final DomainMetadataType defaultUom = getDefaultOrFirstDomain()
                    .map(LiteralDataDomain::getUOM)
                    .orElse(null);
            if (defaultUom != null) {
                uoMs = new SupportedUOMs(defaultUom, Arrays.asList(defaultUom));
            }
        }
        return uoMs;
    }

    /**
     * Sets the value of the uoMs property.
     *
     * @param value
     *     allowed object is
     *     {@link SupportedUOMs }
     * @deprecated WPS 1.0 retro-compatibility
     */
    @Deprecated
    public void setUOMs(final SupportedUOMs value) {
        this.uoMs = value;
    }

    /**
     * @param isParentOutput the isParentOutput to set
     */
    public void setIsParentOutput(boolean isParentOutput) {
        this.isParentOutput = isParentOutput;
    }
}

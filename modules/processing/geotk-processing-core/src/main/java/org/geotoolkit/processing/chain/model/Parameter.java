/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.util.Utilities;

/**
 * Description of a process parameter.
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {

    @XmlAttribute(name = "code")
    private String code;
    @XmlElement(name = "type")
    private ClassFull type;
    private String title;
    private String remarks;
    private Object defaultValue;
    private int minOccurs;
    private int maxOccurs;
    private Map<String, Object> userMap;
    private List<ParameterFormat> formats;

    private Parameter() {
    }

    public Parameter(final Parameter toCopy){
        if (toCopy != null) {
            this.code = toCopy.code;
            this.type = toCopy.type;
            this.title = toCopy.title;
            this.remarks = toCopy.remarks;
            this.defaultValue = toCopy.defaultValue;
            this.minOccurs = toCopy.minOccurs;
            this.maxOccurs = toCopy.maxOccurs;
        }
    }

    public Parameter(final String code, final Class type, final String title, final String remarks, final int minOccurs, final int maxOccurs) {
        this(code, type, title, remarks, minOccurs, maxOccurs, null);
    }

    public Parameter(final String code, final Class type, final String title, final String remarks, final int minOccurs, final int maxOccurs, final Object defaultValue) {
        this.code = code;
        this.title = title;
        this.type = new ClassFull(type);
        this.maxOccurs = maxOccurs;
        this.minOccurs = minOccurs;
        this.remarks = remarks;
        this.defaultValue = defaultValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public ClassFull getType() {
        return type;
    }

    public void setType(final ClassFull type) {
        this.type = type;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the defaultValue
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the minOccurs
     */
    public int getMinOccurs() {
        return minOccurs;
    }

    /**
     * @param minOccurs the minOccurs to set
     */
    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    /**
     * @return the maxOccurs
     */
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * @param maxOccurs the maxOccurs to set
     */
    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    /**
     * @return the userMap
     */
    public Map<String, Object> getUserMap() {
        return userMap;
    }

    /**
     * @param userMap the userMap to set
     */
    public void setUserMap(Map<String, Object> userMap) {
        this.userMap = userMap;
    }

    /**
     * @return the formats
     */
    public List<ParameterFormat> getFormats() {
        return formats;
    }

    /**
     * @param formats the formats to set
     */
    public void setFormats(List<ParameterFormat> formats) {
        this.formats = formats;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Parameter) {
            final Parameter that = (Parameter) obj;
            return Objects  .equals(this.code, that.code) &&
                   Objects  .equals(this.defaultValue, that.defaultValue) &&
                   Utilities.equals(this.maxOccurs, that.maxOccurs) &&
                   Utilities.equals(this.minOccurs, that.minOccurs) &&
                   Objects  .equals(this.remarks, that.remarks) &&
                   Objects  .equals(this.userMap, that.userMap) &&
                   Objects  .equals(this.formats, that.formats) &&
                   Objects  .equals(this.type, that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 43;
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ParameterDto]");
        sb.append("code:").append(code).append('\n');
        if (title != null) {
            sb.append("title:").append(title).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (remarks != null) {
            sb.append("remarks:").append(remarks).append('\n');
        }
        sb.append("minOccurs:").append(minOccurs).append('\n');
        sb.append("maxOccurs:").append(maxOccurs).append('\n');
        sb.append("defaultValue:").append(defaultValue).append('\n');
        if (userMap != null) {
            sb.append("userMap:\n");
            for (Entry<String, Object> e : userMap.entrySet()) {
                sb.append(e.getKey()).append(":").append(e.getValue()).append('\n');
            }
        }
        if (formats != null) {
            sb.append("formats:\n");
            for (ParameterFormat e : formats) {
                sb.append(e).append('\n');
            }
        }
        return sb.toString();
    }
}

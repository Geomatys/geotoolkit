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

import java.io.StringWriter;
import java.util.Objects;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.ows.xml.OWSMarshallerPool;
import org.geotoolkit.wps.xml.v200.DataOutput;
import org.geotoolkit.wps.xml.v200.LiteralValue;

/**
 * OutputInfo
 */
public class OutputInfo {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wps.json");

    private String id = null;

    private ValueType value;

    public OutputInfo() {

    }

    public OutputInfo(DataOutput that) {
        if (that != null) {
            this.id = that.getId();
            if (that.getData() != null && !that.getData().getContent().isEmpty()) {
                if (that.getData().getContent().size() > 1 ) {
                    throw new IllegalArgumentException("Multiple result is not yet handle for json formats");
                }
                Object r = that.getData().getContent().get(0);
                if (r instanceof LiteralValue) {
                    r = ((LiteralValue)r).getValue();
                } else if (r instanceof BoundingBox) {
                    BoundingBox bbox = (BoundingBox) r;
                    if ("text/xml".equals(that.getData().getMimeType()) || "application/xml".equals(that.getData().getMimeType())) {
                        try {
                            Marshaller m = OWSMarshallerPool.getInstance().acquireMarshaller();
                            StringWriter sw = new StringWriter();
                            m.marshal(r, sw);
                            r = sw.toString();
                            OWSMarshallerPool.getInstance().recycle(m);
                        } catch (JAXBException ex) {
                            LOGGER.warning("JAXB exception while marshalling OWS boundingBox into json output");
                        }
                    } else {
                        r = new BoundingBoxInput(bbox);
                    }

                } else if (r instanceof AbstractGeometry) {
                    if ("text/xml".equals(that.getData().getMimeType()) || "application/xml".equals(that.getData().getMimeType())) {
                        try {
                            Marshaller m = GMLMarshallerPool.getInstance().acquireMarshaller();
                            StringWriter sw = new StringWriter();
                            m.marshal(r, sw);
                            r = sw.toString();
                            GMLMarshallerPool.getInstance().recycle(m);
                        } catch (JAXBException ex) {
                            LOGGER.warning("JAXB exception while marshalling GML geomtry into json output");
                        }
                    }
                }
                this.value = new ValueType(r, null);
            } else if (that.getReference() != null) {
                this.value = new ValueType(null, that.getReference().getHref());
            }
        }
    }

    public OutputInfo(String id, String inlineValue, String href) {
        this.id = id;
        this.value = new ValueType(inlineValue, href);
    }

    public OutputInfo id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
  *
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the value
     */
    public ValueType getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(ValueType value) {
        this.value = value;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OutputInfo outputInfo = (OutputInfo) o;
        return Objects.equals(this.id, outputInfo.id)
                && Objects.equals(this.value, outputInfo.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OutputInfo {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

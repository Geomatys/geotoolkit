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
package org.geotoolkit.process.chain.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.opengis.parameter.ParameterDescriptor;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DataLink {
    @XmlAttribute(name = "sourceId")
    private int sourceId;
    @XmlAttribute(name = "sourceCode")
    private String sourceCode;
    @XmlAttribute(name = "targetId")
    private int targetId;
    @XmlAttribute(name = "targetCode")
    private String targetCode;

    private DataLink() {
    }

    public DataLink(DataLink toCopy) {
        this.sourceId = toCopy.sourceId;
        this.sourceCode = toCopy.sourceCode;
        this.targetId = toCopy.targetId;
        this.targetCode = toCopy.targetCode;
    }

    public DataLink(int inId, String inCode, int outId, ParameterDescriptor paramDesc) {
        this(inId, inCode, outId, paramDesc.getName().getCode());
    }

    public DataLink(int inId, ParameterDescriptor inCode, int outId, String paramDesc) {
        this(inId, inCode.getName().getCode(), outId, paramDesc);
    }

    public DataLink(int inId, ParameterDescriptor inCode, int outId, ParameterDescriptor paramDesc) {
        this(inId, inCode.getName().getCode(), outId, paramDesc.getName().getCode());
    }

    public DataLink(int inId, String inCode, int outId, String outCode) {
        this.sourceId = inId;
        this.sourceCode = inCode;
        this.targetId = outId;
        this.targetCode = outCode;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    /**
     * @return source pointed by this link
     */
    public Object getSource(final Chain chain) {
        //source is an input param
        if (sourceId == Integer.MIN_VALUE) {
            for (Parameter param : chain.getInputs()) {
                if (param.getCode().equals(sourceCode)) {
                    return param;
                }
            }
        }
        for (Element desc : chain.getElements()) {
            if (desc.getId() == sourceId) {
                return desc;
            }
        }
        for (Constant cst : chain.getConstants()) {
            if (cst.getId() == sourceId) {
                return cst;
            }
        }
        return null;
    }

    /**
     * @return target pointed by this link
     */
    public Object getTarget(final Chain seq) {
        //targer is an output param
        if (targetId == Integer.MAX_VALUE) {
            for (Parameter param : seq.getOutputs()) {
                if (param.getCode().equals(targetCode)) {
                    return param;
                }
            }
        }
        for (Element desc : seq.getElements()) {
            if (desc.getId() == targetId) {
                return desc;
            }
        }
        for (Constant cst : seq.getConstants()) {
            if (cst.getId() == targetId) {
                return cst;
            }
        }
        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DataLink) {
            final DataLink that = (DataLink) obj;
            return Objects  .equals(this.sourceCode, that.sourceCode) &&
                                   (this.sourceId == that.sourceId) &&
                   Objects  .equals(this.targetCode, that.targetCode) &&
                                   (this.targetId == that.targetId);
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[LinkDto]");
        sb.append("sourceId:").append(sourceId).append('\n');
        sb.append("targetId:").append(targetId).append('\n');
        if (sourceCode != null) {
            sb.append("sourceCode:").append(sourceCode).append('\n');
        }
        if (targetCode != null) {
            sb.append("targetCode:").append(targetCode).append('\n');
        }
        return sb.toString();
    }

}

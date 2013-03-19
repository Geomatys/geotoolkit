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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FlowLink {
    @XmlAttribute(name = "sourceId")
    private int sourceId;
    @XmlAttribute(name = "targetId")
    private int targetId;

    private FlowLink() {
    }

    public FlowLink(final FlowLink toCopy) {
        if (toCopy != null) {
            this.sourceId = toCopy.sourceId;
            this.targetId = toCopy.targetId;
        }
    }

    public FlowLink(int inId, int outId) {
        this.sourceId = inId;
        this.targetId = outId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    /**
     * @return source pointed by this link
     */
    public Object getSource(final Chain chain) {
        //source is an input param
        if (sourceId == Integer.MIN_VALUE) {
            return ChainElement.BEGIN;
        }
        for (ChainElement desc : chain.getChainElements()) {
            if (desc.getId() == sourceId) {
                return desc;
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
            return ChainElement.END;
        }
        for (ChainElement desc : seq.getChainElements()) {
            if (desc.getId() == targetId) {
                return desc;
            }
        }
        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FlowLink) {
            final FlowLink that = (FlowLink) obj;
            return Utilities.equals(this.sourceId, that.sourceId) && Utilities.equals(this.targetId, that.targetId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.sourceId;
        hash = 37 * hash + this.targetId;
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ExecutionLinkDto]");
        sb.append("sourceId:").append(sourceId).append('\n');
        sb.append("targetId:").append(targetId).append('\n');
        return sb.toString();
    }
    
}

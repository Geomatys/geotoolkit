/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLColorModelDirect implements XMLColorModel {
    
    @XmlElement(name="ColorSpace")
    private int colorSpace;
    @XmlElement(name="Bits")
    private int bits;
    @XmlElement(name="RMask")
    private int rmask;
    @XmlElement(name="GMask")
    private int gmask;
    @XmlElement(name="BMask")
    private int bmask;
    @XmlElement(name="AMask")
    private int amask;
    @XmlElement(name="HasAlpha")
    private boolean hasAlpha;
    @XmlElement(name="AlphaPremultiplied")
    private boolean alphaPremultiplied;
    @XmlElement(name="TransferType")
    private int transferType;

    public int getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(int colorSpace) {
        this.colorSpace = colorSpace;
    }
    
    public int getBits() {
        return bits;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }

    public boolean isHasAlpha() {
        return hasAlpha;
    }

    public void setHasAlpha(boolean hasAlpha) {
        this.hasAlpha = hasAlpha;
    }

    public boolean isAlphaPremultiplied() {
        return alphaPremultiplied;
    }

    public void setAlphaPremultiplied(boolean alphaPremultiplied) {
        this.alphaPremultiplied = alphaPremultiplied;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }
    
    public ColorModel buildColorModel(){
        final ColorSpace cs = ColorSpace.getInstance(colorSpace);
        return new DirectColorModel(cs, bits, rmask, gmask, bmask, amask, alphaPremultiplied, transferType);
    }
    
    /**
     * Copy informations from given color model.
     * @param cm 
     */
    public void fill(DirectColorModel cm){
        colorSpace = cm.getColorSpace().getType();
        bits = cm.getPixelSize();
        rmask = cm.getRedMask();
        gmask = cm.getGreenMask();
        bmask = cm.getBlueMask();
        amask = cm.getAlphaMask();
        hasAlpha = cm.hasAlpha();
        alphaPremultiplied = cm.isAlphaPremultiplied();
        transferType = cm.getTransferType();
    }
    
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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

package org.geotoolkit.observation.model;

import java.util.Objects;

/**
 *
 * @author guilhem
 */
public class TextEncoderProperties {

    public final static TextEncoderProperties DEFAULT_ENCODING = new TextEncoderProperties(".", ",", "@@");

    public final static TextEncoderProperties CSV_ENCODING     = new TextEncoderProperties(".", ",", "\n");

    private String tokenSeparator;
    private String blockSeparator;
    private String decimalSeparator;

    private TextEncoderProperties() {}

    public TextEncoderProperties(String decimalSeparator, String tokenSeparator, String blockSeparator) {
        this.tokenSeparator = tokenSeparator;
        this.blockSeparator = blockSeparator;
        this.decimalSeparator = decimalSeparator;
    }

    public String getTokenSeparator() {
        return tokenSeparator;
    }

    public void setTokenSeparator(String tokenSeparator) {
        this.tokenSeparator = tokenSeparator;
    }

    public String getBlockSeparator() {
        return blockSeparator;
    }

    public void setBlockSeparator(String blockSeparator) {
        this.blockSeparator = blockSeparator;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof TextEncoderProperties that) {
            return Objects.equals(this.blockSeparator,   that.blockSeparator) &&
                   Objects.equals(this.decimalSeparator, that.decimalSeparator) &&
                   Objects.equals(this.tokenSeparator,   that.tokenSeparator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.tokenSeparator);
        hash = 59 * hash + Objects.hashCode(this.blockSeparator);
        hash = 59 * hash + Objects.hashCode(this.decimalSeparator);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TextEncoderProperties]\n");
        if (blockSeparator != null) {
            sb.append("blockSeparator: ").append(blockSeparator).append("\n");
        }
        if (tokenSeparator != null) {
            sb.append("tokenSeparator: ").append(tokenSeparator).append("\n");
        }
        if (decimalSeparator != null) {
            sb.append("decimalSeparator: ").append(decimalSeparator).append("\n");
        }
        return sb.toString();
    }
}

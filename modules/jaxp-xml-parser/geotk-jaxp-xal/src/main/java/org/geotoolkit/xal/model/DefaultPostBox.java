/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.xal.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultPostBox implements PostBox {

    private List<GenericTypedGrPostal> addressLines;
    private PostBoxNumber postBoxNumber;
    private PostBoxNumberPrefix postBoxNumberPrefix;
    private PostBoxNumberSuffix postBoxNumberSuffix;
    private PostBoxNumberExtension postBoxNumberExtension;
    private Firm firm;
    private PostalCode postalCode;
    private String type;
    private String indicator;

    public DefaultPostBox(){
        this.addressLines = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param postBoxNumber
     * @param postBoxNumberPrefix
     * @param postBoxNumberSuffix
     * @param postBoxNumberExtension
     * @param firm
     * @param postalCode
     * @param type
     * @param indicator
     */
    public DefaultPostBox(List<GenericTypedGrPostal> addressLines, PostBoxNumber postBoxNumber,
            PostBoxNumberPrefix postBoxNumberPrefix, PostBoxNumberSuffix postBoxNumberSuffix,
            PostBoxNumberExtension postBoxNumberExtension, Firm firm,
            PostalCode postalCode, String type, String indicator){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.postBoxNumber = postBoxNumber;
        this.postBoxNumberPrefix = postBoxNumberPrefix;
        this.postBoxNumberSuffix = postBoxNumberSuffix;
        this.postBoxNumberExtension = postBoxNumberExtension;
        this.firm = firm;
        this.postalCode = postalCode;
        this.type = type;
        this.indicator = indicator;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumber getPostBoxNumber() {return this.postBoxNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberPrefix getPostBoxNumberPrefix() {return this.postBoxNumberPrefix;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberSuffix getPostBoxNumberSuffix() {return this.postBoxNumberSuffix;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberExtension getPostBoxNumberExtension() {return this.postBoxNumberExtension;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Firm getFirm() {return this.firm;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getIndicator() {return this.indicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressLines(List<GenericTypedGrPostal> addressLines) {
        this.addressLines = addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostBoxNumber(PostBoxNumber postBoxNumber) {
        this.postBoxNumber = postBoxNumber;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostBoxNumberPrefix(PostBoxNumberPrefix postBoxNumberPrefix) {
        this.postBoxNumberPrefix = postBoxNumberPrefix;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostBoxNumberSuffix(PostBoxNumberSuffix postBoxNumberSuffix) {
        this.postBoxNumberSuffix = postBoxNumberSuffix;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostBoxNumberExtension(PostBoxNumberExtension postBoxNumberExtension) {
        this.postBoxNumberExtension = postBoxNumberExtension;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

}

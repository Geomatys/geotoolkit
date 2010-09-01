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
 */
public class DefaultPostTown implements PostTown{

    private List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> postTownNames;
    private PostTownSuffix postTownSuffix;
    private String type;

    public DefaultPostTown(){
        this.addressLines = EMPTY_LIST;
        this.postTownNames = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param postTownNames
     * @param postTownSuffix
     * @param type
     */
    public DefaultPostTown(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> postTownNames, PostTownSuffix postTownSuffix, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.postTownNames = (postTownNames == null) ? EMPTY_LIST : postTownNames;
        this.postTownSuffix = postTownSuffix;
        this.type = type;
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
    public List<GenericTypedGrPostal> getPostTownNames() {return this.postTownNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostTownSuffix getPostTownSuffix(){return this.postTownSuffix;}

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
    public void setAddressLines(List<GenericTypedGrPostal> addressLines) {
        this.addressLines = addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostTownNames(List<GenericTypedGrPostal> postTownNames) {
        this.postTownNames = postTownNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostTownSuffix(PostTownSuffix postTownSuffix) {
        this.postTownSuffix = postTownSuffix;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

}

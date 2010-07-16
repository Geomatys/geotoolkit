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
package org.geotoolkit.data.xal.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultThoroughfareNumberTo implements ThoroughfareNumberTo {

    private final List<Object> content;
    private final GrPostal grPostal;

    /**
     *
     * @param content
     * @param grPostal
     * @throws XalException
     */
    public DefaultThoroughfareNumberTo(List<Object> content, GrPostal grPostal) throws XalException{
        this.content = (content == null) ? EMPTY_LIST : this.verifContent(content);
        this.grPostal = grPostal;
    }

    /**
     *
     * @param content
     * @return
     * @throws XalException
     */
    private List<Object> verifContent(List<Object> content) throws XalException{
        for (Object object : content){
            if(!(object instanceof String)
                    && !(object instanceof GenericTypedGrPostal)
                    && !(object instanceof ThoroughfareNumberPrefix)
                    && !(object instanceof ThoroughfareNumber)
                    && !(object instanceof ThoroughfareNumberSuffix))
                throw new XalException("This kind of content ("+object.getClass()+") is not allowed here : "+this.getClass());
        }
        return content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getContent() {return this.content;}


    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}

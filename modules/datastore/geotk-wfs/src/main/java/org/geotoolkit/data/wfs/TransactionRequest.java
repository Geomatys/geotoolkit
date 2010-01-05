/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.data.wfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.geotoolkit.wfs.xml.v110.AllSomeType;


/**
 * WFS Transaction mutable request interface.
 * The request shall be correctly configured before calling the getResponse method.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface TransactionRequest {


    /**
     *
     * In order for a client application to operate upon locked feature instances,
     * the Transaction request must include the LockId element.
     * The content of this element must be the lock identifier the client application obtained from a previous
     * GetFeatureWithLock or LockFeature operation.
     *
     * If the correct lock identifier is specified the Web Feature Service knows that the client application may
     * operate upon the locked feature instances.
     *
     * No LockId element needs to be specified to operate upon unlocked features.
     *
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLockId();

    /**
     *
     * In order for a client application to operate upon locked feature instances,
     * the Transaction request must include the LockId element.
     * The content of this element must be the lock identifier the client application obtained from a previous
     * GetFeatureWithLock or LockFeature operation.
     *
     * If the correct lock identifier is specified the Web Feature Service knows that the client application may
     * operate upon the locked feature instances.
     *
     * No LockId element needs to be specified to operate upon unlocked features.
     *
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLockId(String value);

    /**
     * Gets the value of the insertOrUpdateOrDelete property.
     *
     */
    public List<Object> getInsertOrUpdateOrDelete();

    /**
     * Gets the value of the releaseAction property.
     *
     * @return
     *     possible object is
     *     {@link AllSomeType }
     *
     */
    public AllSomeType getReleaseAction();

    /**
     * Sets the value of the releaseAction property.
     *
     * @param value
     *     allowed object is
     *     {@link AllSomeType }
     *
     */
    public void setReleaseAction(AllSomeType value);

    InputStream getResponse() throws IOException;

}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

import java.util.List;
import org.geotoolkit.client.Request;


/**
 * WFS Transaction mutable request interface.
 * The request shall be correctly configured before calling the getResponse method.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface TransactionRequest extends Request {


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
    String getLockId();

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
     */
    void setLockId(String value);

    /**
     * Gets the value of the insertOrUpdateOrDelete property.
     */
    List<TransactionElement> elements();

    /**
     * Gets the value of the releaseAction property.
     */
    ReleaseAction getReleaseAction();

    /**
     * Sets the value of the releaseAction property.
     */
    void setReleaseAction(ReleaseAction value);

}

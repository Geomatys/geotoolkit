/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.io.IOException;
import org.geotoolkit.data.session.ContentException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Provides the ability to write Features information.
 *
 * <p>
 * Capabilities:
 * </p>
 *
 * <ul>
 * <li>
 * Similar API to FeatureReader
 * </li>
 * <li>
 * After aquiring a feature using next() you may call remove() or after
 * modification write().  If you do not call one of these two methods before
 * calling hasNext(), or next() for that matter, the feature will be left
 * unmodified.
 * </li>
 * <li>
 * This API allows modification, and Filter based modification to be written.
 * Please see AbstractDataStore for examples of implementing common
 * opperations using this API.
 * </li>
 * <li>
 * In order to add new Features, FeatureWriters capable of accepting new
 * content allow next() to be called when hasNext() is <code>false</code> to
 * allow new feature creation. These changes
 * </li>
 * </ul>
 *
 * <p>
 * One thing that is really nice about the approach to adding content is that
 * the generation of FID is not left in the users control.
 * </p>
 *
 * @author Ian Schneider
 * @author Jody Garnett, Refractions Research
 * @version $Id$
 * @module pending
 */
public interface FeatureWriter<T extends FeatureType, F extends Feature> extends FeatureIterator<F>{

    /**
     * FeatureType this reader has been configured to create.
     *
     * @return FeatureType this writer has been configured to create.
     */
    T getFeatureType();

    /**
     * Reads a Feature from the underlying AttributeReader.
     *
     * <p>
     * This method may return a Feature even though hasNext() returns
     * <code>false</code>, this allows FeatureWriters to provide an ability to
     * append content.
     * </p>
     *
     * @return Feature from Query, or newly appended Feature
     *
     * @throws IOException DOCUMENT ME!
     */
    @Override
    F next() throws ContentException;

    /**
     * Removes current Feature, must be called before hasNext.
     *
     * <p>
     * FeatureWriters will need to allow all FeatureSources of the same
     * typeName to issue a FeatureEvent event of type
     * <code>FeatureEvent.FEATURES_REMOVED</code> when this method is called.
     * </p>
     *
     * <p>
     * If this FeatureWriter is opperating against a Transaction
     * FEATURES_REMOVED events should only be sent to FeatureSources operating
     * on the same Transaction. When Transaction commit() is called other
     * FeatureSources will be informed of the modifications.
     * </p>
     *
     * <p>
     * When the current Feature has been provided as new content, this method
     * "cancels" the add opperation (and notification needed).
     * </p>
     *
     * @throws IOException DOCUMENT ME!
     */
    @Override
    void remove() throws ContentException;

    /**
     * Wrties the current Feature, must be called before hasNext.
     *
     * <p>
     * FeautreWriters will need to allow FeatureSources of the same typeName to
     * issue a FeatureEvent:
     * </p>
     *
     * <ul>
     * <li>
     * FeatureEvent.FEATURES_ADDED: when next() has been called with hasNext()
     * equal to <code>false</code>.
     * </li>
     * <li>
     * FeatureEvent.FEATURES_MODIFIED: when next has been called with hasNext()
     * equal to <code>true</code> and the resulting Feature has indeed been
     * modified.
     * </li>
     * </ul>
     *
     * <p>
     * If this FeatureWriter is opperating against a Transaction the
     * FEATURES_MODIFIED or FEATURES_ADDED events should only be sent to
     * FeatureSources opperating on the same Transaction. When Transaction
     * commit() is called other FeatureSources will be informed of the
     * modifications.
     * </p>
     *
     * <p>
     * If you have not called write() when you call hasNext() or next(), no
     * modification will occur().
     * </p>
     *
     * @throws IOException
     */
    void write() throws ContentException;

}

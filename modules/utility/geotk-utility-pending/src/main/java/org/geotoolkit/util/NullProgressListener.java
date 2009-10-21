/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;


/**
 * A default progress listener implementation suitable for
 * subclassing.
 * <p>
 * This implementation supports cancelation and getting/setting the description.
 * The default implementations of the other methods do nothing.
 * </p>
 *
 * @module pending
 * @since 2.2
 * @source $URL$
 * @version $Id$
 */
public class NullProgressListener implements ProgressListener {
    /**
     * Description of the undergoing action.
     */
    private String description;

    /**
     * {@code true} if the action is canceled.
     */
    private boolean canceled = false;

    /**
     * Creates a null progress listener with no description.
     */
    public NullProgressListener() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void started() {
        //do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void progress(final float percent) {
        //do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getProgress() {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void complete() {
        //do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        //do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setCanceled(final boolean cancel) {
        this.canceled = cancel;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void warningOccurred(final String source, final String location, final String warning) {
        //do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void exceptionOccurred(final Throwable exception) {
        //do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InternationalString getTask() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTask(final InternationalString task) {
        // do nothing
    }
}

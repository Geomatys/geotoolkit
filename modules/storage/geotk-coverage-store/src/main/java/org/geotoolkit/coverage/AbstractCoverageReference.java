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
package org.geotoolkit.coverage;

import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.TableColumn;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.storage.DefaultDataNode;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCoverageReference extends DefaultDataNode implements CoverageReference {

    protected final CoverageStore store;
    protected final Name name;

    /**
     *
     * @param store can be null
     * @param name never null
     */
    public AbstractCoverageReference(CoverageStore store, Name name) {
        ArgumentChecks.ensureNonNull("name",name);
        setValue(TableColumn.NAME, name.getLocalPart());
        this.store = store;
        this.name = name;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public CoverageStore getStore() {
        return store;
    }

    /**
     * Default recycle implementation.
     * Dispose the reader.
     *
     * @param reader
     */
    @Override
    public void recycle(CoverageReader reader) {
        dispose(reader);
    }

    /**
     * Default recycle implementation.
     * Dispose the writer.
     *
     * @param writer
     */
    @Override
    public void recycle(GridCoverageWriter writer) {
        try {
            writer.dispose();
        } catch (CoverageStoreException ex) {
            Logging.getLogger(getClass()).log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    protected CoverageStoreManagementEvent firePyramidAdded(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidAddEvent(this, getName(), pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidUpdated(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidUpdateEvent(this, getName(), pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent firePyramidDeleted(final String pyramidId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createPyramidDeleteEvent(this, getName(), pyramidId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicAdded(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicAddEvent(this, getName(), pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicUpdated(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicUpdateEvent(this, getName(), pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreManagementEvent fireMosaicDeleted(final String pyramidId, final String mosaicId){
        final CoverageStoreManagementEvent event = CoverageStoreManagementEvent.createMosaicDeleteEvent(this, getName(), pyramidId, mosaicId);
        sendStructureEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireDataUpdated(){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createDataUpdateEvent(this, getName());
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileAdded(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileAddEvent(this, getName(), pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileUpdated(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileUpdateEvent(this, getName(), pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    protected CoverageStoreContentEvent fireTileDeleted(final String pyramidId, final String mosaicId, final List<Point> tiles){
        final CoverageStoreContentEvent event = CoverageStoreContentEvent.createTileDeleteEvent(this, getName(), pyramidId, mosaicId, tiles);
        sendContentEvent(event);
        return event;
    }

    /**
     * Dispose a reader, trying to properly release sub resources.
     * Best effort.
     *
     * @param reader
     */
    protected void dispose(CoverageReader reader) {
        try {
//            //try to close sub stream
//            Object input = reader.getInput();
//            if(input instanceof ImageReader){
//                final ImageReader ireader = (ImageReader)input;
//                ImageIOUtilities.releaseReader(ireader);
//            }else if(input instanceof InputStream){
//                final InputStream stream = (InputStream) input;
//                stream.close();
//            }else if(input instanceof ImageInputStream){
//                final ImageInputStream stream = (ImageInputStream) input;
//                stream.close();
//            }

            reader.dispose();

        } catch (CoverageStoreException ex) {
            Logging.getLogger(getClass()).log(Level.WARNING, ex.getMessage(), ex);
        }
    }

}

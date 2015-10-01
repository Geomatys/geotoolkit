/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage.decorator;

import java.awt.Image;
import java.io.File;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.coverage.AbstractCoverageReference;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.opengis.metadata.content.CoverageDescription;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DecoratorCoverageReference extends AbstractCoverageReference{

    protected final CoverageReference ref;

    //source unmodified informations
    protected GeneralGridGeometry refGridGeom;
    protected List<GridSampleDimension> refDims;


    //overrided informations
    protected GeneralGridGeometry overrideGridGeom;
    protected List<GridSampleDimension> overrideDims;

    public DecoratorCoverageReference(CoverageReference ref, CoverageStore store) {
        super(store, ref.getName());
        this.ref = ref;
    }

    public CoverageReference getDecorated(){
        return ref;
    }

    private void loadRefData(int index) throws CoverageStoreException{
        if(refGridGeom==null){
            final GridCoverageReader reader = ref.acquireReader();
            refGridGeom = reader.getGridGeometry(index);
            refDims = reader.getSampleDimensions(index);
            ref.recycle(reader);
        }
    }

    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException{
        loadRefData(index);
        if(overrideGridGeom!=null){
            return overrideGridGeom;
        }else{
            return refGridGeom;
        }
    }

    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException{
        loadRefData(index);
        if(overrideDims!=null){
            return overrideDims;
        }else{
            return refDims;
        }
    }

    @Override
    public int getImageIndex() {
        return ref.getImageIndex();
    }

    @Override
    public CoverageDescription getMetadata() {
        return ref.getMetadata();
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        return ref.isWritable();
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        return new DecoratorCoverageReader(this);
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void recycle(CoverageReader reader) {
        ((DecoratorCoverageReader)reader).dispose();
    }

    @Override
    public void recycle(GridCoverageWriter writer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return ref.getLegend();
    }

}

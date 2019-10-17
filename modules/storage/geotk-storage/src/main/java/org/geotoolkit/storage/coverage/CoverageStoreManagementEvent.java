/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import org.apache.sis.storage.Resource;
import org.geotoolkit.storage.StorageEvent;
import org.opengis.util.GenericName;

/**
 * Coverage store structure change event.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageStoreManagementEvent extends StorageEvent {

    public static enum Type{
        COVERAGE_ADD,
        COVERAGE_UPDATE,
        COVERAGE_DELETE,
        PYRAMID_ADD,
        PYRAMID_UPDATE,
        PYRAMID_DELETE,
        MOSAIC_ADD,
        MOSAIC_UPDATE,
        MOSAIC_DELETE
    };

    private final Type type;
    private final GenericName coverageName;
    private final String pyramidId;
    private final String mosaicId;


    public CoverageStoreManagementEvent(Resource source, Type type, GenericName name, String pyramidId, String mosaicId) {
        super(source);
        this.type = type;
        this.coverageName = name;
        this.pyramidId = pyramidId;
        this.mosaicId = mosaicId;
    }

    public Type getType() {
        return type;
    }

    public GenericName getCoverageName() {
        return coverageName;
    }

    public String getPyramidId() {
        return pyramidId;
    }

    public String getMosaicId() {
        return mosaicId;
    }

    @Override
    public CoverageStoreManagementEvent copy(final Resource source){
        return new CoverageStoreManagementEvent(source, type, coverageName, pyramidId, mosaicId);
    }

    public static CoverageStoreManagementEvent createCoverageAddEvent(
            final Resource source, final GenericName name){
        return new CoverageStoreManagementEvent(source, Type.COVERAGE_ADD, name, null, null);
    }

    public static CoverageStoreManagementEvent createCoverageUpdateEvent(
            final Resource source, final GenericName name){
        return new CoverageStoreManagementEvent(source, Type.COVERAGE_UPDATE, name, null, null);
    }

    public static CoverageStoreManagementEvent createCoverageDeleteEvent(
            final Resource source, final GenericName name){
        return new CoverageStoreManagementEvent(source, Type.COVERAGE_DELETE, name, null, null);
    }

    public static CoverageStoreManagementEvent createPyramidAddEvent(
            final Resource source, final GenericName name, final String pyramidId){
        return new CoverageStoreManagementEvent(source, Type.PYRAMID_ADD, name, pyramidId, null);
    }

    public static CoverageStoreManagementEvent createPyramidUpdateEvent(
            final Resource source, final GenericName name, final String pyramidId){
        return new CoverageStoreManagementEvent(source, Type.PYRAMID_UPDATE, name, pyramidId, null);
    }

    public static CoverageStoreManagementEvent createPyramidDeleteEvent(
            final Resource source, final GenericName name, final String pyramidId){
        return new CoverageStoreManagementEvent(source, Type.PYRAMID_DELETE, name, pyramidId, null);
    }

    public static CoverageStoreManagementEvent createMosaicAddEvent(
            final Resource source, final GenericName name, final String pyramidId, final String mosaicId){
        return new CoverageStoreManagementEvent(source, Type.PYRAMID_ADD, name, pyramidId, mosaicId);
    }

    public static CoverageStoreManagementEvent createMosaicUpdateEvent(
            final Resource source, final GenericName name, final String pyramidId, final String mosaicId){
        return new CoverageStoreManagementEvent(source, Type.PYRAMID_UPDATE, name, pyramidId, mosaicId);
    }

    public static CoverageStoreManagementEvent createMosaicDeleteEvent(
            final Resource source, final GenericName name, final String pyramidId, final String mosaicId){
        return new CoverageStoreManagementEvent(source, Type.PYRAMID_DELETE, name, pyramidId, mosaicId);
    }

}

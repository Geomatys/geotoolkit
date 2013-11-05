/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.iso8211.DataRecord;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.s57.model.BaseAttribute;
import org.geotoolkit.data.s57.model.DataSetIdentification;
import org.geotoolkit.data.s57.model.DataSetParameter;
import org.geotoolkit.data.s57.model.FeatureRecord;
import org.geotoolkit.data.s57.model.Pointer;
import org.geotoolkit.data.s57.model.S57Object;
import org.geotoolkit.data.s57.model.S57Reader;
import org.geotoolkit.data.s57.model.VectorRecord;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.util.Converters;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57FeatureReader implements FeatureReader{

    private static final GeometryFactory GF = new GeometryFactory();

    private final S57FeatureStore store;

    //searched type
    private final FeatureType featureType;
    private final PropertyDescriptor vectorType;
    private final Map<Integer,PropertyDescriptor> properties;
    /**
     * May be null if type is the global S-57 type.
     */
    private final Integer s57TypeCode;
    //S-57 metadata/description records
    private DataSetIdentification datasetIdentification;
    private DataSetParameter datasetParameter;
    private int coordFactor;
    private int soundingFactor;
    // S-57 objects
    private final S57Reader mreader;
    private final Map<Pointer,VectorEntity> spatials = new HashMap<>();

    private Feature feature;

    public S57FeatureReader(S57FeatureStore store,FeatureType type, Integer s57typeCode, S57Reader mreader,
            DataSetIdentification datasetIdentification,DataSetParameter datasetParameter) throws DataStoreException {
        this.store = store;
        this.featureType = type;
        this.vectorType = type.getDescriptor(S57Constants.PROPERTY_VECTORS);
        this.s57TypeCode = s57typeCode;
        this.mreader = mreader;
        this.datasetIdentification = datasetIdentification;
        this.datasetParameter = datasetParameter;
        this.coordFactor = datasetParameter.coordFactor;
        this.soundingFactor = datasetParameter.soundingFactor;
        this.mreader.setDsid(datasetIdentification);

        properties = TypeBanks.getAllProperties();

        if(s57typeCode!=null){
            mreader.setPredicate(new S57Reader.Predicate() {
                @Override
                public boolean match(DataRecord record) {
                    final Field root = record.getRootField();
                    final Field firstField = root.getFields().get(0);
                    final String tag = firstField.getType().getTag();
                    if(FeatureRecord.FRID.equals(tag)){
                        Long obj = (Long) firstField.getSubField(FeatureRecord.FRID_OBJL).getValue();
                        return s57TypeCode.longValue() == obj;
                    }else if(VectorRecord.VRID.equals(tag)){
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public FeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        findNext();
        if(feature == null){
            throw new FeatureStoreRuntimeException("No more features");
        }
        Feature f = feature;
        feature = null;
        return f;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        findNext();
        return feature != null;
    }

    private void findNext() throws FeatureStoreRuntimeException {
        if(feature != null) return;

        try{
            while(feature==null && mreader.hasNext()){
                final S57Object modelObj = mreader.next();
                if(modelObj instanceof VectorRecord){
                    final VectorRecord rec = (VectorRecord) modelObj;
                    final Pointer key = rec.generatePointer();
                    final VectorEntity previous = spatials.put(key, new VectorEntity(rec));
                    if(previous!=null){
                        throw new FeatureStoreRuntimeException("Duplicate vector record : "+key);
                    }
                }else if(modelObj instanceof FeatureRecord){
                    final FeatureRecord rec = (FeatureRecord) modelObj;
                    //only the given type
                    if(s57TypeCode!=null && rec.code != s57TypeCode) continue;
                    feature = toFeature(rec);
                }
            }
        }catch(IOException | DataStoreException ex){
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }

    }

    private Feature toFeature(final FeatureRecord record) throws DataStoreException{

        final FeatureType currentType;
        if(featureType.isAbstract()){
            //search appropriate S-57 type from code
            currentType = TypeBanks.getFeatureType(record.code,datasetParameter.buildCoordinateReferenceSystem());
        }else{
            currentType = featureType;
        }

        //we must not append the version in the id, otherwise we can't find older versions
        //final String id = record.identifier.agency.ascii+"."+String.valueOf(record.id)+".v"+record.version;
        final String id = record.identifier.agency.ascii+"."+String.valueOf(record.id);
        final Feature f =  FeatureUtilities.defaultFeature(currentType, id);
        final S57VersionedFeature versionned = new S57VersionedFeature(store, currentType.getName(), f.getIdentifier());
        f.getUserData().put(FeatureUtilities.ATT_VERSIONING, versionned);

        //read attributes
        for(FeatureRecord.Attribute att : record.attributes){
            final PropertyDescriptor desc = properties.get(att.code);
            readAttribute(att, desc, f.getProperty(desc.getName().getLocalPart()));
        }
        for(FeatureRecord.NationalAttribute att : record.nattributes){
            final PropertyDescriptor desc = properties.get(att.code);
            readAttribute(att, desc, f.getProperty(desc.getName().getLocalPart()));
        }

        //rebuild geometry and vector attributes
        final CoordinateReferenceSystem crs = currentType.getCoordinateReferenceSystem();
        final S57Constants.DataStructure structure = datasetIdentification.information.dataStructure;
        if(structure == S57Constants.DataStructure.NONREV){
            //no geometry
        }else if(structure == S57Constants.DataStructure.SPAGHETTI){
            rebuildSpaghetti(record,f,crs);
        }else if(structure == S57Constants.DataStructure.CHAINNODE){
            rebuildChained(record,f,crs);
        }else if(structure == S57Constants.DataStructure.GRAPH){
            throw new FeatureStoreRuntimeException("S-57 Graph topology mode not supported.");
        }else if(structure == S57Constants.DataStructure.FULL){
            throw new FeatureStoreRuntimeException("S-57 Full topology mode not supported.");
        }

        return f;
    }

    private static void readAttribute(BaseAttribute att, PropertyDescriptor desc, Property prop){
        final Class binding = desc.getType().getBinding();
        if(binding.isArray()){
            //enumeration list type
            final String[] val = att.value.split(",");
            prop.setValue(val);
        }else{
            final Object val = Converters.convert(att.value, binding);
            prop.setValue(val);
        }
    }

    private void rebuildSpaghetti(final FeatureRecord record, Feature feature, CoordinateReferenceSystem crs){

        Geometry geometry = null;
        if(S57Constants.Primitive.PRIMITIVE_POINT == record.primitiveType){
            final List<Coordinate> coords = new ArrayList<>();
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorEntity rec = spatials.get(sp);
                //rebuild complex vector record attribute
                feature.getProperties().add(rec.toComplexAttribute(crs));
                //rebuild the global geometry
                coords.addAll(rec.getCoordinates());
            }
            if(!coords.isEmpty()){
                geometry = GF.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
            }
        }else if(S57Constants.Primitive.PRIMITIVE_LINE == record.primitiveType){
            final List<LineString> lines = new ArrayList<>();
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorEntity rec = spatials.get(sp);
                //rebuild complex vector record attribute
                feature.getProperties().add(rec.toComplexAttribute(crs));
                //rebuild the global geometry
                final List<Coordinate> coords = rec.getCoordinates();
                if(coords.size() == 1){ //we need at least 2 points
                    coords.add((Coordinate)coords.get(0).clone());
                }
                if(!coords.isEmpty()){
                    final LineString line = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
                    lines.add(line);
                }
            }
            if(!lines.isEmpty()){
                geometry = GF.createMultiLineString(lines.toArray(new LineString[lines.size()]));
            }
        }else if(S57Constants.Primitive.PRIMITIVE_AREA == record.primitiveType){
            final List<LinearRing> interiors = new ArrayList<>();
            LinearRing exterior = null;
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorEntity rec = spatials.get(sp);
                //rebuild complex vector record attribute
                feature.getProperties().add(rec.toComplexAttribute(crs));
                //rebuild the global geometry
                final List<Coordinate> coords = rec.getCoordinates();

                if(coords.isEmpty()){
                   //an empty ring ?
                    continue;
                }

                final LinearRing ring = toRing(coords);
                if(sp.usage == S57Constants.Usage.EXTERIOR || sp.usage == S57Constants.Usage.EXTERIOR_TRUNCATED){
                    exterior = ring;
                }else{
                    interiors.add(ring);
                }

            }

            if(exterior != null){
                geometry = GF.createPolygon(exterior,interiors.toArray(new LinearRing[interiors.size()]));
            }
        }

        //set the geometry on the feature
        if(geometry != null){
            JTS.setCRS(geometry, crs);
            feature.getProperty(S57Constants.PROPERTY_GEOMETRY).setValue(geometry);
        }
    }

    private void rebuildChained(final FeatureRecord record, Feature feature, CoordinateReferenceSystem crs){

        Geometry geometry = null;
        if(S57Constants.Primitive.PRIMITIVE_POINT == record.primitiveType){
            final List<Coordinate> coords = new ArrayList<>();
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorEntity rec = spatials.get(sp);
                //rebuild complex vector record attribute
                feature.getProperties().add(rec.toComplexAttribute(crs));
                //rebuild the global geometry
                coords.addAll(rec.getCoordinates());
            }
            if(!coords.isEmpty()){
                geometry = GF.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
            }
        }else if(S57Constants.Primitive.PRIMITIVE_LINE == record.primitiveType){
            final List<Coordinate> inprogress = new ArrayList<>();
            Pointer startNode = null;
            Pointer endNode = null;

            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorEntity edge = spatials.get(sp);
                final Pointer edgeStart = edge.record.getEdgeBeginNode();
                final Pointer edgeEnd = edge.record.getEdgeEndNode();
                final VectorEntity startPoint = spatials.get(edgeStart);
                final VectorEntity endPoint = spatials.get(edgeEnd);
                final List<Coordinate> scoords = edge.getCoordinates();

                //rebuild complex vector record attribute
                feature.getProperties().add(edge.toComplexAttribute(crs));
                if(startPoint != null) feature.getProperties().add(startPoint.toComplexAttribute(crs));
                if(endPoint != null) feature.getProperties().add(endPoint.toComplexAttribute(crs));

                if(startNode == null){
                    //first exterior segment
                    inprogress.addAll(scoords);
                    startNode = edgeStart;
                    endNode = edgeEnd;
                    inprogress.add(0,startPoint.getNodeCoordinate());
                    inprogress.add(endPoint.getNodeCoordinate());
                }else{
                    if(edgeStart.equals(startNode)){
                        Collections.reverse(scoords);
                        inprogress.addAll(0,scoords);
                        inprogress.add(0,endPoint.getNodeCoordinate());
                        startNode = edgeEnd;
                    }else if(edgeStart.equals(endNode)){
                        inprogress.addAll(scoords);
                        inprogress.add(endPoint.getNodeCoordinate());
                        endNode = edgeEnd;
                    }else if(edgeEnd.equals(startNode)){
                        inprogress.addAll(0,scoords);
                        inprogress.add(0,startPoint.getNodeCoordinate());
                        startNode = edgeStart;
                    }else if(edgeEnd.equals(endNode)){
                        Collections.reverse(scoords);
                        inprogress.addAll(scoords);
                        inprogress.add(startPoint.getNodeCoordinate());
                        endNode = edgeStart;
                    }else{
                        throw new FeatureStoreRuntimeException("Segment not connected");
                    }
                }
            }

            if(inprogress.size() == 1){ //we need at least 2 points
                inprogress.add((Coordinate)inprogress.get(0).clone());
            }
            if(!inprogress.isEmpty()){
                geometry = GF.createLineString(inprogress.toArray(new Coordinate[inprogress.size()]));
            }

        }else if(S57Constants.Primitive.PRIMITIVE_AREA == record.primitiveType){

            final List<LinearRing> interiors = new ArrayList<>();
            LinearRing outter = null;
            List<Coordinate> inprogress = new ArrayList<>();
            Pointer startNode = null;
            Pointer endNode = null;

            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorEntity edge = spatials.get(sp);
                final Pointer edgeStart = edge.record.getEdgeBeginNode();
                final Pointer edgeEnd = edge.record.getEdgeEndNode();
                final VectorEntity startPoint = spatials.get(edgeStart);
                final VectorEntity endPoint = spatials.get(edgeEnd);
                final List<Coordinate> scoords = edge.getCoordinates();

                //rebuild complex vector record attribute
                feature.getProperties().add(edge.toComplexAttribute(crs));
                if(startPoint != null) feature.getProperties().add(startPoint.toComplexAttribute(crs));
                if(endPoint != null) feature.getProperties().add(endPoint.toComplexAttribute(crs));

                if(startNode == null){
                    //first exterior segment
                    inprogress.addAll(scoords);
                    startNode = edgeStart;
                    endNode = edgeEnd;
                    inprogress.add(0,startPoint.getNodeCoordinate());
                    inprogress.add(endPoint.getNodeCoordinate());
                }else{
                    if(edgeStart.equals(startNode)){
                        Collections.reverse(scoords);
                        inprogress.addAll(0,scoords);
                        inprogress.add(0,endPoint.getNodeCoordinate());
                        startNode = edgeEnd;
                    }else if(edgeStart.equals(endNode)){
                        inprogress.addAll(scoords);
                        inprogress.add(endPoint.getNodeCoordinate());
                        endNode = edgeEnd;
                    }else if(edgeEnd.equals(startNode)){
                        inprogress.addAll(0,scoords);
                        inprogress.add(0,startPoint.getNodeCoordinate());
                        startNode = edgeStart;
                    }else if(edgeEnd.equals(endNode)){
                        Collections.reverse(scoords);
                        inprogress.addAll(scoords);
                        inprogress.add(startPoint.getNodeCoordinate());
                        endNode = edgeStart;
                    }else{
                        throw new FeatureStoreRuntimeException("Segment not connected");
                    }
                }

                if(startNode.equals(endNode)){
                    //finish ring
                    LinearRing ring = toRing(inprogress);
                    if(sp.usage == S57Constants.Usage.EXTERIOR || sp.usage == S57Constants.Usage.EXTERIOR_TRUNCATED){
                        outter = ring;
                    }else{
                        interiors.add(ring);
                    }
                    inprogress.clear();
                    startNode = null;
                    endNode = null;
                }
            }

            geometry = GF.createPolygon(outter,interiors.toArray(new LinearRing[interiors.size()]));
        }

        if(geometry != null){
            JTS.setCRS(geometry, crs);
            feature.getProperty(S57Constants.PROPERTY_GEOMETRY).setValue(geometry);
        }
    }

    private static LinearRing toRing(List<Coordinate> coords){
        while(coords.size() < 4 || !coords.get(0).equals2D(coords.get(coords.size()-1))){
            coords.add((Coordinate)coords.get(0).clone());
        }
        return GF.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
    }

    @Override
    public void close() throws FeatureStoreRuntimeException {
        try {
            mreader.dispose();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("writing not supported");
    }

    private class VectorEntity{

        public final VectorRecord record;
        private ComplexAttribute vf = null;
        private Coordinate coord = null;
        private List<Coordinate> coords = null;

        public VectorEntity(VectorRecord record) {
            this.record = record;
        }

        private Coordinate getNodeCoordinate(){
            if(coord==null){
                coord = record.getNodeCoordinate(coordFactor);
            }
            return coord;
        }

        private List<Coordinate> getCoordinates(){
            if(this.coords==null){
                this.coords = record.getCoordinates(new ArrayList<Coordinate>(), coordFactor, soundingFactor);
            }
            return new ArrayList<>(this.coords);
        }

        /**
        * Rebuild a complex property from VectorRecord.
        * @param record
        * @param type
        * @return
        */
        private ComplexAttribute toComplexAttribute(CoordinateReferenceSystem crs){
            if(vf == null){
                vf = (ComplexAttribute)FeatureUtilities.defaultProperty(vectorType, ""+record.id);
                //rebuild attributs
                for(VectorRecord.Attribute att : record.attributes){
                    final PropertyDescriptor desc = properties.get(att.code);
                    final Property prop = FeatureUtilities.defaultProperty(desc);
                    readAttribute(att, desc, prop);
                    vf.getProperties().add(prop);
                }
                //rebuild geometry
                final List<Coordinate> coords = getCoordinates();
                final Geometry geom;
                if(coords.isEmpty()){
                    //do not create geometry, leave it null
                    geom = null;
                }else if(coords.size()==1){
                    //create a Point
                    geom = GF.createPoint(coords.get(0));
                }else{
                    //create a MultiPoint
                    geom = GF.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
                }
                if(geom != null){
                    JTS.setCRS(geom, crs);
                    vf.getProperty(S57Constants.PROPERTY_GEOMETRY).setValue(geom);
                }
            }
            return vf;
        }

    }

}

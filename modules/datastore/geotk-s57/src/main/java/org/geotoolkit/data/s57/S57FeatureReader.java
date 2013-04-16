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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.iso8211.DataRecord;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.s57.model.DataSetIdentification;
import org.geotoolkit.data.s57.model.DataSetParameter;
import org.geotoolkit.data.s57.model.FeatureRecord;
import org.geotoolkit.data.s57.model.Pointer;
import org.geotoolkit.data.s57.model.S57ModelObject;
import org.geotoolkit.data.s57.model.S57ModelObjectReader;
import org.geotoolkit.data.s57.model.VectorRecord;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.util.Converters;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57FeatureReader implements FeatureReader{

    private static final GeometryFactory GF = new GeometryFactory();
        
    //searched type
    private final FeatureType type;
    private final Map<Integer,PropertyDescriptor> properties = new HashMap<Integer, PropertyDescriptor>();
    private final int s57TypeCode;    
    //S-57 metadata/description records
    private DataSetIdentification datasetIdentification;
    private DataSetParameter datasetParameter;
    // S-57 objects
    private final S57ModelObjectReader mreader;
    private final Map<Pointer,VectorRecord> spatials = new HashMap<Pointer,VectorRecord>();
    
    private Feature feature;

    public S57FeatureReader(FeatureType type, int s57typeCode, S57ModelObjectReader mreader,
            DataSetIdentification datasetIdentification,DataSetParameter datasetParameter) {
        this.type = type;
        this.s57TypeCode = s57typeCode;
        this.mreader = mreader;
        this.datasetIdentification = datasetIdentification;
        this.datasetParameter = datasetParameter;
                
        for(PropertyDescriptor desc :type.getDescriptors()){
            Integer code = (Integer) desc.getUserData().get(S57FeatureStore.S57TYPECODE);
            properties.put(code, desc);
        }
        
        mreader.setPredicate(new S57ModelObjectReader.Predicate() {
            @Override
            public boolean match(DataRecord record) {
                final Field root = record.getRootField();
                final Field firstField = root.getFields().get(0);
                final String tag = firstField.getType().getTag();
                if(FeatureRecord.FRID.equalsIgnoreCase(tag)){
                    Long obj = (Long) firstField.getSubField(FeatureRecord.FRID_OBJL).getValue();
                    return s57TypeCode == obj;
                }else if(VectorRecord.VRID.equalsIgnoreCase(tag)){
                    return true;
                }
                return false;
            }
        });
        
    }
    
    @Override
    public FeatureType getFeatureType() {
        return type;
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
                final S57ModelObject modelObj = mreader.next();
                if(modelObj instanceof VectorRecord){
                    final VectorRecord rec = (VectorRecord) modelObj;
                    final Pointer key = rec.generatePointer();
                    final VectorRecord previous = spatials.put(key, rec);
                    if(previous!=null){
                        throw new FeatureStoreRuntimeException("Duplicate vector record : "+key);
                    }
                }else if(modelObj instanceof FeatureRecord){
                    final FeatureRecord rec = (FeatureRecord) modelObj;
                    //only the given type
                    if(rec.code != s57TypeCode) continue;
                    feature = toFeature(rec);
                }
            }
        }catch(IOException ex){
            throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
        }
        
    }
    
    private final List<Coordinate> coords = new ArrayList<Coordinate>();
    private final List<LineString> lines = new ArrayList<LineString>();
    private final List<LinearRing> interiors = new ArrayList<LinearRing>();
            
    private Feature toFeature(final FeatureRecord record){
        final Feature f =  FeatureUtilities.defaultFeature(type, String.valueOf(record.id));
        System.out.println("-------");
        System.out.println(record.id+" "+record.updateInstruction+" "+record.code+" "+record.identifier.number);
        
        //read attributes
        for(FeatureRecord.Attribute att : record.attributes){
            final PropertyDescriptor desc = properties.get(att.code);
            final Object val = Converters.convert(att.value, desc.getType().getBinding());
            f.getProperty(desc.getName().getLocalPart()).setValue(val);
        }
        for(FeatureRecord.NationalAttribute att : record.nattributes){
            final PropertyDescriptor desc = properties.get(att.code);
            final Object val = Converters.convert(att.value, desc.getType().getBinding());
            f.getProperty(desc.getName().getLocalPart()).setValue(val);
        }
        
        //rebuild geometry
        Geometry geometry = null;
        final S57Constants.DataStructure structure = datasetIdentification.information.dataStructure;
        if(structure == S57Constants.DataStructure.NONREV){
            //no geometry
        }else if(structure == S57Constants.DataStructure.SPAGHETTI){
            geometry = rebuildSpaghetti(record);
        }else if(structure == S57Constants.DataStructure.CHAINNODE){
            geometry = rebuildChained(record);
        }else if(structure == S57Constants.DataStructure.GRAPH){
            throw new FeatureStoreRuntimeException("S-57 Graph topology mode not supported.");
        }else if(structure == S57Constants.DataStructure.FULL){
            throw new FeatureStoreRuntimeException("S-57 Full topology mode not supported.");
        }
        
        if(geometry != null){
            JTS.setCRS(geometry, type.getCoordinateReferenceSystem());
            f.getProperty("spatial").setValue(geometry);
        }
        
        return f;
    }
        
    private Geometry rebuildSpaghetti(final FeatureRecord record){
        coords.clear();
        lines.clear();
        interiors.clear();
        
        Geometry geometry = null;
        if(S57Constants.Primitive.PRIMITIVE_POINT == record.primitiveType){
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorRecord rec = spatials.get(sp);
                fillCoordinates(rec, coords,false);
            }
            if(!coords.isEmpty()){
                geometry = GF.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
            }
        }else if(S57Constants.Primitive.PRIMITIVE_LINE == record.primitiveType){
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                coords.clear();
                final VectorRecord rec = spatials.get(sp);
                fillCoordinates(rec, coords,false);
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
            LinearRing exterior = null;
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                coords.clear();
                final VectorRecord rec = spatials.get(sp);
                fillCoordinates(rec, coords,false);
                
                if(coords.isEmpty()){
                   //an empty ring ?
                    continue;
                }
                
                while(coords.size() < 4 || !coords.get(0).equals2D(coords.get(coords.size()-1))){
                    coords.add((Coordinate)coords.get(0).clone());
                }
                
                final LinearRing ring = GF.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
                if(sp.usage == S57Constants.Usage.EXTERIOR){
                    exterior = ring;
                }else if(sp.usage == S57Constants.Usage.EXTERIOR_TRUNCATED){
                    throw new FeatureStoreRuntimeException("TRUNCATED segements not supported yet.");
                }else{
                    interiors.add(ring);
                }
                
            }
                        
            if(exterior != null){
                geometry = GF.createPolygon(exterior,interiors.toArray(new LinearRing[interiors.size()]));
            }
        }
        
        return geometry;
    }
    
    private Geometry rebuildChained(final FeatureRecord record){
        coords.clear();
        lines.clear();
        interiors.clear();
        
        Geometry geometry = null;
        if(S57Constants.Primitive.PRIMITIVE_POINT == record.primitiveType){
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorRecord rec = spatials.get(sp);
                fillCoordinates(rec, coords,false);
            }
            if(!coords.isEmpty()){
                geometry = GF.createMultiPoint(coords.toArray(new Coordinate[coords.size()]));
            }
        }else if(S57Constants.Primitive.PRIMITIVE_LINE == record.primitiveType){
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                coords.clear();
                final VectorRecord rec = spatials.get(sp);
                fillCoordinates(rec, coords,false);
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
            
            LinearRing outter = null;
            List<Coordinate> inbuild = new ArrayList<Coordinate>();
            Pointer startNode = null;
            Pointer endNode = null;
                        
            for(FeatureRecord.SpatialPointer sp : record.spatialPointers){
                final VectorRecord edge = spatials.get(sp);
                final Pointer edgeStart = edge.getEdgeBeginNode();
                final Pointer edgeEnd = edge.getEdgeEndNode();
                final VectorRecord startPoint = spatials.get(edgeStart);
                final VectorRecord endPoint = spatials.get(edgeEnd);
                final List<Coordinate> scoords = edge.getEdgeCoordinates(datasetParameter.coordFactor);
                             
                if(sp.usage == S57Constants.Usage.EXTERIOR || sp.usage == S57Constants.Usage.EXTERIOR_TRUNCATED){
                    
                    if(startNode == null){
                        //first exterior segment
                        inbuild.addAll(scoords);
                        startNode = edgeStart;
                        endNode = edgeEnd;
                        inbuild.add(0,startPoint.getNodeCoordinate(datasetParameter.coordFactor));
                        inbuild.add(endPoint.getNodeCoordinate(datasetParameter.coordFactor));
                    }else{
                        if(edgeStart.equals(startNode)){
                            Collections.reverse(scoords);                            
                            inbuild.addAll(0,scoords);
                            inbuild.add(0,endPoint.getNodeCoordinate(datasetParameter.coordFactor));
                            startNode = edgeEnd;                            
                        }else if(edgeStart.equals(endNode)){
                            inbuild.addAll(scoords);
                            inbuild.add(endPoint.getNodeCoordinate(datasetParameter.coordFactor));
                            endNode = edgeEnd;
                        }else if(edgeEnd.equals(startNode)){
                            inbuild.addAll(0,scoords);
                            inbuild.add(0,startPoint.getNodeCoordinate(datasetParameter.coordFactor));
                            startNode = edgeStart;
                        }else if(edgeEnd.equals(endNode)){
                            Collections.reverse(scoords);
                            inbuild.addAll(scoords);
                            inbuild.add(startPoint.getNodeCoordinate(datasetParameter.coordFactor));
                            endNode = edgeStart;
                        }else{
                            throw new FeatureStoreRuntimeException("Segment not connected");
                        }
                    }
                    
                }else{
                    scoords.add(0,startPoint.getNodeCoordinate(datasetParameter.coordFactor));
                    scoords.add(endPoint.getNodeCoordinate(datasetParameter.coordFactor));
                    interiors.add(toRing(scoords));
                }
                                
            }
            
            outter = toRing(inbuild);
            geometry = GF.createPolygon(outter,interiors.toArray(new LinearRing[interiors.size()]));
        }
        
        return geometry;
    }
    
    private static LinearRing toRing(List<Coordinate> coords){
        while(coords.size() < 4 || !coords.get(0).equals2D(coords.get(coords.size()-1))){
            coords.add((Coordinate)coords.get(0).clone());
        }
        return GF.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
    }
    
    /**
     * 
     * @param rec
     * @param coords
     * @param reverse insert at the begin in inverse order
     */
    private void fillCoordinates(VectorRecord rec, List<Coordinate> coords, boolean reverse){
        if(reverse){
            //insert in reverse order
            for(int i=rec.coords2D.size()-1; i>=0; i--){
                final VectorRecord.Coordinate2D c = rec.coords2D.get(i);
                coords.add(0,new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor));
            }
            for(int i=rec.coords3D.size()-1; i>=0; i--){
                final VectorRecord.Coordinate3D c = rec.coords3D.get(i);
                coords.add(0,new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor, c.y/datasetParameter.soundingFactor));
            }
        }else{
            for(int i=rec.coords2D.size()-1; i>=0; i--){
                final VectorRecord.Coordinate2D c = rec.coords2D.get(i);
                coords.add(0,new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor));
            }
            for(int i=rec.coords3D.size()-1; i>=0; i--){
                final VectorRecord.Coordinate3D c = rec.coords3D.get(i);
                coords.add(0,new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor, c.y/datasetParameter.soundingFactor));
            }
        }
    }
    
    private List<Coordinate> fillCoordinates(VectorRecord edge){
        System.out.println("== "+edge.id);
        List<Coordinate> coords = new ArrayList<Coordinate>();
        
        fillCoordinates(edge, coords, false);
        
        //explore left side
        Pointer leftPointer = edge.getEdgeBeginNode();
        
        if(leftPointer != null){
            System.out.println("<< "+leftPointer);
            VectorRecord leftspatial = spatials.get(leftPointer);
            fillCoordinates(leftspatial, coords, true);
        }
        
        //explore right side
        Pointer rightPointer = edge.getEdgeEndNode();
        if(rightPointer != null){
            System.out.println(">> "+rightPointer);
            VectorRecord rightspatial = spatials.get(rightPointer);
            fillCoordinates(rightspatial, coords, false);
        }
        
        return coords;
    }
    
    
//    private void fillCoordinates(VectorRecord rec, List<Coordinate> coords, Boolean forward, S57Constants.Mask mask){
//        System.out.println("-> "+rec.id +" "+forward);
//        
//        if(mask == S57Constants.Mask.MASK){
//            System.out.println(">>>>>>> ARGGGG");
//        }
//        if(forward!= null && !forward){
//            //insert in reverse order
//            for(int i=rec.coords2D.size()-1; i>=0; i--){
//                final VectorRecord.Coordinate2D c = rec.coords2D.get(i);
//                coords.add(0,new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor));
//            }
//            for(int i=rec.coords3D.size()-1; i>=0; i--){
//                final VectorRecord.Coordinate3D c = rec.coords3D.get(i);
//                coords.add(0,new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor, c.y/datasetParameter.soundingFactor));
//            }
//        }else{
//            for(VectorRecord.Coordinate2D c : rec.coords2D){
//                coords.add(new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor));
//            }
//            for(VectorRecord.Coordinate3D c : rec.coords3D){
//                coords.add(new Coordinate(c.x/datasetParameter.coordFactor, c.y/datasetParameter.coordFactor, c.y/datasetParameter.soundingFactor));
//            }
//        }
//        
//        //get previous or next segments
//        for(VectorRecord.RecordPointer recp : rec.records){
//            if(recp.refid == rec.id){
//                //refer to himself, must be an end segment
//                continue;
//            }
//            if(recp.topology == S57Constants.Topology.TOPI_BEGIN_NODE){
//                if(forward == null || !forward){
//                    fillCoordinates((VectorRecord)spatials.get(new SpatialKey(recp.refid, recp.type)), coords, false, recp.mask);
//                }
//            }else if(recp.topology == S57Constants.Topology.TOPI_END_NODE){
//                if(forward == null || forward){
//                    fillCoordinates((VectorRecord)spatials.get(new SpatialKey(recp.refid, recp.type)), coords, true, recp.mask);
//                }
//            }
//        }
//                
//    }
    
    
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
    
}

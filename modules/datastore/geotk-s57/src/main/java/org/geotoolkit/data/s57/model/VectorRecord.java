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
package org.geotoolkit.data.s57.model;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.SubField;
import org.geotoolkit.data.s57.S57Constants;
import static org.geotoolkit.data.s57.S57Constants.*;
import static org.geotoolkit.data.s57.model.S57Object.*;
import org.geotoolkit.io.LEDataInputStream;

/**
 * S-57 Vector/Spatial record.
 *
 * @author Johann Sorel (Geomatys)
 */
public class VectorRecord extends S57Object {

    //7.7.1.1 Vector record identifier field structure
    public static final String VRID = "VRID";
    public static final String VRID_RCNM = "RCNM";
    public static final String VRID_RCID = "RCID";
    /** record version */
    public static final String VRID_RVER = "RVER";
    /** record update instruction */
    public static final String VRID_RUIN = "RUIN";

    /** RVER */
    public int version;
    /** RUIN */
    public UpdateInstruction updateInstruction;
    /** ATTV */
    public final List<Attribute> attributes = new ArrayList<>();
    /** VRPC */
    public RecordPointerControl recordPointerControl;
    /** VRPT */
    public final List<RecordPointer> records = new ArrayList<>();
    /** SGCC */
    public CoordinateControl coordinateControl;
    /** SG2D/SG3D */
    public final List<Coordinate2D> coords = new ArrayList<>();
    /** ARCC */
    public List<Arc> arcs;

    /**
     * Shortcut to access edge begin node.
     * May return null if vector is not an edge.
     * @return
     */
    public RecordPointer getEdgeBeginNode(){
        for(RecordPointer rp : records){
            if(rp.topology == Topology.TOPI_BEGIN_NODE){
                return rp;
            }
        }
        return null;
    }

    /**
     * Shortcut to access edge begin node.
     * May return null if vector is not an edge.
     * @return
     */
    public RecordPointer getEdgeEndNode(){
        for(RecordPointer rp : records){
            if(rp.topology == Topology.TOPI_END_NODE){
                return rp;
            }
        }
        return null;
    }

    /**
     * If vector is an edge/nodes, return it's coordinates.
     * @return Coordinate
     */
    public List<Coordinate> getCoordinates(List<Coordinate> coords, double coordFactor,double soundingFactor){
        if(coords == null) coords = new ArrayList<>();
        for(Coordinate2D c : this.coords){
            if(c.is3D){
                coords.add(new Coordinate(c.x/coordFactor, c.y/coordFactor, c.z/soundingFactor));
            }else{
                coords.add(new Coordinate(c.x/coordFactor, c.y/coordFactor));
            }
        }
        return coords;
    }

    /**
     * If vector is a node, return it's coordinate.
     * @return Coordinate
     */
    public Coordinate getNodeCoordinate(double coordFactor){
        final Coordinate2D c = coords.get(0);
        return new Coordinate(c.x/coordFactor, c.y/coordFactor);
    }

    public static class Attribute extends BaseAttribute {
        //7.7.1.2 Vector record attribute field structure
        public static final String VRID_ATTV = "ATTV";
        public static final String VRID_ATTV_ATTL = "ATTL";
        public static final String VRID_ATTV_ATVL = "ATVL";

        @Override
        protected String getKeyTag() {
            return VRID_ATTV_ATTL;
        }

        @Override
        protected String getValueTag() {
            return VRID_ATTV_ATVL;
        }

    }

    public static class RecordPointerControl extends BaseControl {
        //7.7.1.3 Vector record pointer control field structure
        public static final String VRID_VRPC = "VRPC";
        public static final String VRID_VRPC_VPUI = "VPUI";
        public static final String VRID_VRPC_VPIX = "VPIX";
        public static final String VRID_VRPC_NVPT = "NVPT";

        @Override
        protected String getUpdateTag() {
            return VRID_VRPC_VPUI;
        }

        @Override
        protected String getIndexTag() {
            return VRID_VRPC_VPIX;
        }

        @Override
        protected String getNumberTag() {
            return VRID_VRPC_NVPT;
        }
    }

    public static class RecordPointer extends Pointer {
        //7.7.1.4 Vector record pointer field structure
        public static final String VRID_VRPT = "VRPT";
        /** NAME is composed of */
        public static final String VRID_VRPT_NAME = "NAME";
        public static final String VRID_VRPT_ORNT = "ORNT";
        public static final String VRID_VRPT_USAG = "USAG";
        public static final String VRID_VRPT_TOPI = "TOPI";
        public static final String VRID_VRPT_MASK = "MASK";

        //informations
        public Orientation orientation;
        public Usage usage;
        public Topology topology;
        public Mask mask;

        public RecordPointer() {
        }

        public RecordPointer(long id, S57Constants.RecordType type) {
            this.refid = id;
            this.type = type;
        }

        @Override
        public void read(Field isofield) throws IOException {
            read(isofield.getSubFields());
        }

        public void read(List<SubField> subFields) throws IOException {
            for(SubField sf : subFields){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                if(VRID_VRPT_NAME.equals(tag)){
                     if(value instanceof byte[]){
                        final byte[] buffer = (byte[]) value;
                        type = RecordType.valueOf(buffer[0] & 0xff);
                        refid = LEDataInputStream.readUnsignedInt(buffer, 1);
                    }else{
                        //TODO
                        throw new IOException("ASCII Form for NAME not supported yet");
                    }
                }
                else if(VRID_VRPT_ORNT.equals(tag)) orientation = Orientation.valueOf(value);
                else if(VRID_VRPT_USAG.equals(tag)) usage = Usage.valueOf(value);
                else if(VRID_VRPT_TOPI.equals(tag)) topology = Topology.valueOf(value);
                else if(VRID_VRPT_MASK.equals(tag)) mask = Mask.valueOf(value);
            }
        }

        @Override
        public String toString() {
            return "RP:"+type+","+refid+","+orientation+","+usage+","+topology+","+mask;
        }

    }

    public static class CoordinateControl extends BaseControl {
        //7.7.1.5 Coordinate control field structure
        public static final String VRID_SGCC = "SGCC";
        public static final String VRID_SGCC_CCUI = "CCUI";
        public static final String VRID_SGCC_CCIX = "CCIX";
        public static final String VRID_SGCC_CCNC = "CCNC";

        @Override
        protected String getUpdateTag() {
            return VRID_SGCC_CCUI;
        }

        @Override
        protected String getIndexTag() {
            return VRID_SGCC_CCIX;
        }

        @Override
        protected String getNumberTag() {
            return VRID_SGCC_CCNC;
        }

    }

    public static class Coordinate2D extends S57Object {
        //7.7.1.6 2-D Coordinate field structure
        public static final String VRID_SG2D = "SG2D";
        public static final String VRID_SGXD_YCOO = "YCOO";
        public static final String VRID_SGXD_XCOO = "XCOO";
        //7.7.1.7 3-D Coordinate field structure
        public static final String VRID_SG3D = "SG3D";
        /**
        * In the binary implementation, 3-D sounding values are encoded as integers. In order to convert
        * floating-point 3-D (sounding) values to integers (and vice-versa) a multiplication factor is used. The factor
        * is defined by the encoder and held in the “3-D (sounding) Multiplication Factor” [SOMF] subfield. The
        * SOMF subfield applies to the “3-D (sounding) Value” [VE3D] subfield of the “3-D Coordinate” [SG3D] field.
        * The conversion algorithm is defined in clause 2.6.
        */
        public static final String VRID_SG3D_VE3D = "VE3D";

        private final boolean is3D;
        public double x;
        public double y;
        public double z;

        public Coordinate2D(boolean is3D) {
            this.is3D = is3D;
        }

        @Override
        public void read(Field isofield) throws IOException {
            read(isofield.getSubFields());
        }

        public void read(List<SubField> subFields) throws IOException {
            for(SubField sf : subFields){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if(VRID_SGXD_YCOO.equals(tag)) y = toDouble(value);
                else if(VRID_SGXD_XCOO.equals(tag)) x = toDouble(value);
                else if(VRID_SG3D_VE3D.equals(tag)) z = toDouble(value);
            }
        }

        @Override
        public String toString() {
            return "Coord["+x+"  "+y+"  "+z+"]";
        }

    }

    public static class Arc extends S57Object {
        //7.7.1.8 Arc/Curve definition field structure
        public static final String VRID_ARCC = "ARCC";
        public static final String VRID_ARCC_ATYP = "ATYP";
        public static final String VRID_ARCC_SURF = "SURF";
        public static final String VRID_ARCC_ORDR = "ORDR";
        public static final String VRID_ARCC_RESO = "RESO";
        public static final String VRID_ARCC_FPMF = "FPMF";

        public ArcType type;
        public ConstructionSurface surface;
        public int order;
        public double resolution;
        public int factor;

        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if (VRID_ARCC_ATYP.equals(tag)) type = ArcType.valueOf(value);
                else if (VRID_ARCC_SURF.equals(tag)) surface = ConstructionSurface.valueOf(value);
                else if (VRID_ARCC_ORDR.equals(tag)) order = toInteger(value);
                else if (VRID_ARCC_RESO.equals(tag)) resolution = toDouble(value);
                else if (VRID_ARCC_FPMF.equals(tag)) factor = toInteger(value);
            }
        }

        public static class Arc2D extends S57Object {
            //7.7.1.9 Arc coordinates field structure
            public static final String VRID_ARCC_AR2D = "AR2D";
            public static final String VRID_ARCC_AR2D_ENPT = "STPT";
            public static final String VRID_ARCC_AR2D_CDPM = "CTPT";
            public static final String VRID_ARCC_AR2D_CDPR = "ENPT";
            public static final String VRID_ARCC_AR2D_YCOO = "YCOO";
            public static final String VRID_ARCC_AR2D_XCOO = "XCOO";
        }

        public static class Ellipse2D extends S57Object {
            //7.7.1.10 Ellipse coordinates field structure
            public static final String VRID_ARCC_EL2D = "EL2D";
            public static final String VRID_ARCC_EL2D_STPT = "STPT";
            public static final String VRID_ARCC_EL2D_CTPT = "CTPT";
            public static final String VRID_ARCC_EL2D_ENPT = "ENPT";
            public static final String VRID_ARCC_EL2D_CDPM = "CDPM";
            public static final String VRID_ARCC_EL2D_CDPR = "CDPR";
            public static final String VRID_ARCC_EL2D_YCOO = "YCOO";
            public static final String VRID_ARCC_EL2D_XCOO = "XCOO";
        }

        public static class Curve2D extends S57Object {
            //7.7.1.11 Curve coordinates field structure
            public static final String VRID_ARCC_CT2D = "CT2D";
            public static final String VRID_ARCC_CT2D_YCOO = "YCOO";
            public static final String VRID_ARCC_CT2D_XCOO = "XCOO";
        }

    }

    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if (VRID_RCNM.equals(tag)) type = RecordType.valueOf(value);
            else if (VRID_RCID.equals(tag)){
                id = toLong(value);
            }
            else if (VRID_RVER.equals(tag)) version = toInteger(value);
            else if (VRID_RUIN.equals(tag)) updateInstruction = UpdateInstruction.valueOf(value);
        }
        for(Field f : isofield.getFields()){
            final String tag = f.getType().getTag();
            if(Attribute.VRID_ATTV.equals(tag)){
                final Iterator<SubField> sfite = f.getSubFields().iterator();
                while(sfite.hasNext()){
                    final Attribute candidate = new Attribute();
                    candidate.attfLexicalLevel = attfLexicalLevel;
                    candidate.natfLexicalLevel = natfLexicalLevel;
                    candidate.read(Arrays.asList(sfite.next(),sfite.next()));
                    attributes.add(candidate);
                }
            }else if(RecordPointerControl.VRID_VRPC.equals(tag)){
                recordPointerControl = new RecordPointerControl();
                recordPointerControl.read(f);
            }else if(RecordPointer.VRID_VRPT.equals(tag)){
                final Iterator<SubField> sfite = f.getSubFields().iterator();
                while(sfite.hasNext()){
                    final RecordPointer candidate = new RecordPointer();
                    candidate.read(Arrays.asList(sfite.next(),sfite.next(),sfite.next(),sfite.next(),sfite.next()));
                    records.add(candidate);
                }
            }else if(CoordinateControl.VRID_SGCC.equals(tag)){
                coordinateControl = new CoordinateControl();
                coordinateControl.read(f);
            }else if(Coordinate2D.VRID_SG2D.equals(tag)){
                final Iterator<SubField> sfite = f.getSubFields().iterator();
                while(sfite.hasNext()){
                    final Coordinate2D candidate = new Coordinate2D(false);
                    candidate.read(Arrays.asList(sfite.next(),sfite.next()));
                    coords.add(candidate);
                }
            }else if(Coordinate2D.VRID_SG3D.equals(tag)){
                final Iterator<SubField> sfite = f.getSubFields().iterator();
                while(sfite.hasNext()){
                    final Coordinate2D candidate = new Coordinate2D(true);
                    candidate.read(Arrays.asList(sfite.next(),sfite.next(),sfite.next()));
                    coords.add(candidate);
                }
            }else if(Arc.VRID_ARCC.equals(tag)){
                if(arcs==null) arcs = new ArrayList<Arc>();
                final Arc candidate = new Arc();
                candidate.read(f);
                arcs.add(candidate);
            }
        }
    }

}

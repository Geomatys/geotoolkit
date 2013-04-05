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
package org.geotoolkit.data.iso8211;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISO8211Test {
 
    private static String DDR_SAMPLE = 
              "015613LE1 0900201 ! 3404"
            + "00001230000"
            + "00010430123"
            + "DSID1650166"
            + "DSSI1130331"
            + "DSPM1300444"
            + "FRID1000574"
            + "FOID0700674"
            + "ATTF0590744"
            + "NATF0680803"
            + "FFPT0860871"
            + "FSPT0900957"
            + "VRID0781047"
            + "ATTV0581125"
            + "VRPT0761183"
            + "SG2D0481259"
            + "SG3D0531307"
            + "0000;&   "
            + "0001DSID"
                + "DSIDDSSI"
            + "0001DSPM"
            + "0001FRID"
                + "FRIDFOID"
                + "FRIDATTF"
                + "FRIDNATF"
                + "FRIDFFPT"
                + "FRIDFSPT"
            + "0001VRID"
                + "VRIDATTV"
                + "VRIDVRPT"
                + "VRIDSG2D"
                + "VRIDSG3D"
            + "0500;&   ISO 8211 Record Identifier(b12)"
            + "1600;&   Data set identification fieldRCNM!RCID!EXPP!INTU!DSNM!EDTN!UPDN!UADT!ISDT!STED!PRSP!PSDN!PRED!PROF!AGEN!COMT(b11,b14,2b11,3A,2A(8),R(4),b11,2A,b11,b12,A)"
            + "1600;&   Data set structure information fieldDSTR!AALL!NALL!NOMR!NOCR!NOGR!NOLR!NOIN!NOCN!NOED!NOFA(3b11,8b14)"
            + "1600;&   Data set parameter fieldRCNM!RCID!HDAT!VDAT!SDAT!CSCL!DUNI!HUNI!PUNI!COUN!COMF!SOMF!COMT(b11,b14,3b11,b14,4b11,2b14,A)"
            + "1600;&   Feature record identifier fieldRCNM!RCID!PRIM!GRUP!OBJL!RVER!RUIN(b11,b14,2b11,2b12,b11)"
            + "1600;&   Feature object identifier fieldAGEN!FIDN!FIDS(b12,b14,b12)2600;&-A Feature record attribute field*ATTL!ATVL(b12,A)"
            + "2600;&-A Feature record national attribute field*ATTL!ATVL(b12,A)2600;&   Feature record to feature object pointer field*LNAM!RIND!COMT(B(64),b11,A)"
            + "2600;&   Feature record to spatial record pointer field*NAME!ORNT!USAG!MASK(B(40),3b11)1600;&   Vector record identifier fieldRCNM!RCID!RVER!RUIN(b11,b14,b12,b11)"
            + "2600;&   Vector record attribute field*ATTL!ATVL(b12,A)2600;&   Vector record pointer field*NAME!ORNT!USAG!TOPI!MASK(B(40),4b11)"
            + "2500;&   2-D Coordinate field*YCOO!XCOO(2b24)"
            + "2500;&   3-D Coordinate field*YCOO!XCOO!VE3D(3b24)";
    
    @Test
    public void DDRCalculateMetricsTest() throws IOException{
        final ISO8211Reader reader = new ISO8211Reader();
        reader.setInput(new ByteArrayInputStream(DDR_SAMPLE.getBytes(ISO8211Constants.US_ASCII)));
        final DataRecord ddr = reader.getDDR();
        assertEquals(1561, ddr.getRecordLength());
        assertEquals(201,  ddr.getAreaAddress());
        assertEquals(3,    ddr.getFieldLengthSize());
        assertEquals(4,    ddr.getFieldPositionSize());
        assertEquals(0,    ddr.getFieldReserved());
        assertEquals(4,    ddr.getFieldSizeTag());
        
        //check metrics are correctly recalculated
        final DataRecord nddr = new DataRecord();
        nddr.getFieldDescriptions().addAll(ddr.getFieldDescriptions());
        assertEquals(0,    nddr.getRecordLength());
        assertEquals(0,    nddr.getAreaAddress());
        assertEquals(0,    nddr.getFieldLengthSize());
        assertEquals(0,    nddr.getFieldPositionSize());
        assertEquals(0,    nddr.getFieldReserved());
        assertEquals(0,    nddr.getFieldSizeTag());
        nddr.recalculateMetrics();
        assertEquals(1561, nddr.getRecordLength());
        assertEquals(201,  nddr.getAreaAddress());
        assertEquals(3,    nddr.getFieldLengthSize());
        assertEquals(4,    nddr.getFieldPositionSize());
        assertEquals(0,    nddr.getFieldReserved());
        assertEquals(4,    nddr.getFieldSizeTag());
        
    }
    
    @Test
    public void readDataTypeTest() throws IOException{
        final String str = "(b11,2C,b24,3A(8))";
        final List<SubFieldDescription> subfields = FieldValueType.readTypes(str);
        assertNotNull(subfields);
        assertEquals(7, subfields.size());
        
        int index = 0;
        assertEquals(FieldValueType.LE_INTEGER_UNSIGNED, subfields.get(index).getType());
        assertEquals(Integer.valueOf(1), subfields.get(index).getLength());
        index++;
        assertEquals(FieldValueType.LOGICAL, subfields.get(index).getType());
        assertEquals(null, subfields.get(index).getLength());
        index++;
        assertEquals(FieldValueType.LOGICAL, subfields.get(index).getType());
        assertEquals(null, subfields.get(index).getLength());
        index++;
        assertEquals(FieldValueType.LE_INTEGER_SIGNED, subfields.get(index).getType());
        assertEquals(Integer.valueOf(4), subfields.get(index).getLength());
        index++;
        assertEquals(FieldValueType.TEXT, subfields.get(index).getType());
        assertEquals(Integer.valueOf(8), subfields.get(index).getLength());
        index++;
        assertEquals(FieldValueType.TEXT, subfields.get(index).getType());
        assertEquals(Integer.valueOf(8), subfields.get(index).getLength());
        index++;
        assertEquals(FieldValueType.TEXT, subfields.get(index).getType());
        assertEquals(Integer.valueOf(8), subfields.get(index).getLength());
        
    }
    
    @Test
    public void writeDataTypeTest() throws IOException{
        final List<SubFieldDescription> subfields = new ArrayList<SubFieldDescription>();
        subfields.add(new SubFieldDescription(FieldValueType.LE_INTEGER_UNSIGNED, 1));
        subfields.add(new SubFieldDescription(FieldValueType.LOGICAL, null));
        subfields.add(new SubFieldDescription(FieldValueType.LOGICAL, null));
        subfields.add(new SubFieldDescription(FieldValueType.LE_INTEGER_SIGNED, 4));
        subfields.add(new SubFieldDescription(FieldValueType.TEXT, 8));
        subfields.add(new SubFieldDescription(FieldValueType.TEXT, 8));
        subfields.add(new SubFieldDescription(FieldValueType.TEXT, 8));
        
        final String result = FieldValueType.write(subfields);
        assertEquals("(b11,2C,b24,3A(8))", result);
        
    }
    
    
}

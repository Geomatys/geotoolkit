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
package org.geotoolkit.coverage.filestore;

import java.awt.image.DataBuffer;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.opengis.coverage.SampleDimensionType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRootElement(name="SampleDimension")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLSampleDimension {

    @XmlElement(name="name")
    public String name;
    @XmlElement(name="unit")
    public String unit;
    @XmlElement(name="min")
    public Double min;
    @XmlElement(name="max")
    public Double max;
    @XmlElement(name="offset")
    public Double offset;
    @XmlElement(name="scale")
    public Double scale;
    @XmlElement(name="type")
    public String type;

    public void setUnit(Unit unit){
        if(unit==null){
            this.unit = null;
        }else{
            this.unit = unit.toString();
        }
    }

    public Unit getUnit(){
        if(this.unit==null || unit.isEmpty()){
            return Unit.ONE;
        }else{
            try{
                return Unit.valueOf(unit);
            }catch(Exception ex){
                return null;
            }
        }
    }

    public void setSampleType(SampleDimensionType sdt){
        type = sdt.identifier();
    }

    public SampleDimensionType getSampleType(){
        for(SampleDimensionType sdt : SampleDimensionType.values()){
            if(sdt.identifier().equals(type)){
                return sdt;
            }
        }
        throw  new IllegalArgumentException("Unexpected type : "+type);
    }

    public int getDataType(){
        return getDataType(getSampleType());
    }

    public static int getDataType(SampleDimensionType sdt){
        if(SampleDimensionType.REAL_32BITS.equals(sdt)){
            return DataBuffer.TYPE_FLOAT;
        }else if(SampleDimensionType.REAL_64BITS.equals(sdt)){
            return DataBuffer.TYPE_DOUBLE;
        }else if(SampleDimensionType.SIGNED_8BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.SIGNED_16BITS.equals(sdt)){
            return DataBuffer.TYPE_SHORT;
        }else if(SampleDimensionType.SIGNED_32BITS.equals(sdt)){
            return DataBuffer.TYPE_INT;
        }else if(SampleDimensionType.UNSIGNED_1BIT.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_2BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_4BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_8BITS.equals(sdt)){
            return DataBuffer.TYPE_BYTE;
        }else if(SampleDimensionType.UNSIGNED_16BITS.equals(sdt)){
            return DataBuffer.TYPE_USHORT;
        }else if(SampleDimensionType.UNSIGNED_32BITS.equals(sdt)){
            return DataBuffer.TYPE_INT;
        }else {
            throw new IllegalArgumentException("Unexprected data type : "+sdt);
        }
    }

}

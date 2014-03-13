/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.util.List;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.CoverageUtilities;
import org.geotoolkit.coverage.GridSampleDimension;
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
    @XmlElement(name="type")
    public String type;
    @XmlElement(name="category")
    public List<XMLCategory> categories;
    

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
        return CoverageUtilities.getDataType(getSampleType());
    }

    public GridSampleDimension buildSampleDimension(){
        final Category[] cats = new Category[categories.size()];
        for(int i=0;i<cats.length;i++){
            cats[0] = categories.get(i).buildCategory();
        }
        return new GridSampleDimension(name, cats, getUnit());
    }
    
    /**
     * Copy informations from given sample dimension.
     * @param gsd 
     */
    public void fill(final GridSampleDimension gsd){
        categories.clear();
        name = gsd.getDescription().toString();
        setUnit(gsd.getUnits());
        for(Category cat : gsd.getCategories()){
            final XMLCategory xcat = new XMLCategory();
            xcat.fill(cat);
            categories.add(xcat);
        }
    }
    
}

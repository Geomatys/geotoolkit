/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.util.Version;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractSTSRequestById implements STSRequest {

    private String id;

    /**
     * The $expand system query option indicates the related entities to be represented inline.
     * The value of the $expand query option SHALL be a comma separated list of navigation property names.
     * Additionally, each navigation property can be followed by a forward slash and another navigation property to enable identifying a multi-level relationship.
     */
    protected List<String> expand;

    /**
     * The $select system query option requests the service to return only the properties explicitly requested by the client.
     * The value of a $select query option SHALL be a comma-separated list of selection clauses.
     * Each selection clause SHALL be a property name (including navigation property names).
     * In the response, the service SHALL return the specified content, if available, along with any available expanded navigation properties.
     */
    protected List<String> select;

    protected String resultFormat;

    @JsonIgnore
    protected Map<String, String> extraFlag = new HashMap<>();

    public AbstractSTSRequestById() {

    }

    public AbstractSTSRequestById(String id) {
        this.id = id;
    }
    /**
     * @return the expand
     */
    @Override
    public List<String> getExpand() {
        if (expand == null) {
            expand = new ArrayList<>();
        }
        return expand;
    }

    /**
     * @param expand the expand to set
     */
    public void setExpand(List<String> expand) {
        this.expand = expand;
    }

    /**
     * @return the select
     */
    @Override
    public List<String> getSelect() {
        return select;
    }

    /**
     * @param select the select to set
     */
    public void setSelect(List<String> select) {
        this.select = select;
    }

    @Override
    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    @Override
    public String getService() {
        return "STS";
    }

    @Override
    public void setService(String value) {
        // hard coded
    }

    @Override
    public Version getVersion() {
        return new Version("1.0.0");
    }

    @Override
    public void setVersion(String version) {
        // hard coded
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getExtraFlag() {
        if (extraFlag == null)  {
            extraFlag = new HashMap<>();
        }
        return extraFlag;
    }

    public void setExtraFlag(Map<String, String> extraFlag) {
        this.extraFlag = extraFlag;
    }
}

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.request.connectedsystems;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code GET} operation on {@code /observations}: lists Observation resources.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetObservations extends RequestParameters {

    private List<String> id;
    private List<String> q;
    private String phenomenonTime;
    private String resultTime;
    private List<String> dataStream;
    private List<String> foi;
    private List<String> observedProperty;
    private Integer limit;

    /**
     * Only resources with one of the given identifiers are selected. (optional)
     *
     * @return the id
     */
    public List<String> getId() {
        return id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public void setId(List<String> id) {
        this.id = id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public GetObservations id(List<String> id) {
        setId(id);
        return this;
    }

    /**
     * Free-text search on the resource's textual properties. (optional)
     *
     * @return the q
     */
    public List<String> getQ() {
        return q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public void setQ(List<String> q) {
        this.q = q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public GetObservations q(List<String> q) {
        setQ(q);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339, filtering on the phenomenon time. (optional)
     *
     * @return the phenomenonTime
     */
    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * @param phenomenonTime the phenomenonTime to set
     * @see #getPhenomenonTime()
     */
    public void setPhenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    /**
     * @param phenomenonTime the phenomenonTime to set
     * @see #getPhenomenonTime()
     */
    public GetObservations phenomenonTime(String phenomenonTime) {
        setPhenomenonTime(phenomenonTime);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339, filtering on the result time. (optional)
     *
     * @return the resultTime
     */
    public String getResultTime() {
        return resultTime;
    }

    /**
     * @param resultTime the resultTime to set
     * @see #getResultTime()
     */
    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    /**
     * @param resultTime the resultTime to set
     * @see #getResultTime()
     */
    public GetObservations resultTime(String resultTime) {
        setResultTime(resultTime);
        return this;
    }

    /**
     * Only observations associated with one of the given data stream identifiers are selected.
     * (optional)
     *
     * @return the dataStream
     */
    public List<String> getDataStream() {
        return dataStream;
    }

    /**
     * @param dataStream the dataStream to set
     * @see #getDataStream()
     */
    public void setDataStream(List<String> dataStream) {
        this.dataStream = dataStream;
    }

    /**
     * @param dataStream the dataStream to set
     * @see #getDataStream()
     */
    public GetObservations dataStream(List<String> dataStream) {
        setDataStream(dataStream);
        return this;
    }

    /**
     * Only resources associated with one of the given features of interest are selected. (optional)
     *
     * @return the foi
     */
    public List<String> getFoi() {
        return foi;
    }

    /**
     * @param foi the foi to set
     * @see #getFoi()
     */
    public void setFoi(List<String> foi) {
        this.foi = foi;
    }

    /**
     * @param foi the foi to set
     * @see #getFoi()
     */
    public GetObservations foi(List<String> foi) {
        setFoi(foi);
        return this;
    }

    /**
     * Only resources that observe one of the given properties are selected. (optional)
     *
     * @return the observedProperty
     */
    public List<String> getObservedProperty() {
        return observedProperty;
    }

    /**
     * @param observedProperty the observedProperty to set
     * @see #getObservedProperty()
     */
    public void setObservedProperty(List<String> observedProperty) {
        this.observedProperty = observedProperty;
    }

    /**
     * @param observedProperty the observedProperty to set
     * @see #getObservedProperty()
     */
    public GetObservations observedProperty(List<String> observedProperty) {
        setObservedProperty(observedProperty);
        return this;
    }

    /**
     * The optional limit parameter limits the number of items presented in the response. (optional)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetObservations limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}

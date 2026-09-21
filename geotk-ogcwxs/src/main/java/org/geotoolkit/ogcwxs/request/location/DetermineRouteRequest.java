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
package org.geotoolkit.ogcwxs.request.location;

import org.geotoolkit.ols.xml.v121.DistanceUnitType;
import org.geotoolkit.ols.xml.v121.RouteGeometryRequestType;
import org.geotoolkit.ols.xml.v121.RouteHandleType;
import org.geotoolkit.ols.xml.v121.RouteInstructionsRequestType;
import org.geotoolkit.ols.xml.v121.RouteMapRequestType;
import org.geotoolkit.ols.xml.v121.RoutePlanType;

/**
 * Parameters for the OLS Route Service {@code DetermineRouteRequest} operation.
 *
 * <blockquote><q>
 * This service determines a route for a subscriber. The subscriber must use
 * a navigation application to set up the use of the service.
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=46">; OGC 07-074 OpenLS Core, Part 6: Route Service</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DetermineRouteRequest extends AbstractLocationRequest<DetermineRouteRequest> {

    private RouteHandleType routeHandle;
    private RoutePlanType routePlan;
    private RouteInstructionsRequestType routeInstructionsRequest;
    private RouteGeometryRequestType routeGeometryRequest;
    private RouteMapRequestType routeMapRequest;
    private Boolean provideRouteHandle;
    private DistanceUnitType distanceUnit;

    /**
     * Handle of a previously determined route, used instead of {@link #getRoutePlan()}. Optional.
     *
     * @see #routeHandle(RouteHandleType)
     */
    public RouteHandleType getRouteHandle() {
        return routeHandle;
    }

    /**
     * <blockquote><q>
     * Reference to a proviously determined route stored at the Route
     * Determination Service server.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=127">; OGC 07-074 OpenLS Core, DetermineRouteRequestType/RouteHandle</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Exactly one of routeHandle or routePlan must be provided.</li>
     * </ul>
     */
    public DetermineRouteRequest routeHandle(RouteHandleType routeHandle) {
        this.routeHandle = routeHandle;
        return this;
    }

    /**
     * Way points and preferences describing the route to compute. Required unless {@link #getRouteHandle()} is set.
     *
     * @see #routePlan(RoutePlanType)
     */
    public RoutePlanType getRoutePlan() {
        return routePlan;
    }

    /**
     * <blockquote><q>
     * The criteria upon which a route is determined.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=127">; OGC 07-074 OpenLS Core, DetermineRouteRequestType/RoutePlan</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Exactly one of routeHandle or routePlan must be provided.</li>
     * </ul>
     */
    public DetermineRouteRequest routePlan(RoutePlanType routePlan) {
        this.routePlan = routePlan;
        return this;
    }

    /**
     * Turn-by-turn instructions request. Optional.
     *
     * @see #routeInstructionsRequest(RouteInstructionsRequestType)
     */
    public RouteInstructionsRequestType getRouteInstructionsRequest() {
        return routeInstructionsRequest;
    }

    /**
     * <blockquote><q>
     * Request parameters for turn-by-turn route directions and advisories
     * formatted for presentation.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=127">; OGC 07-074 OpenLS Core, DetermineRouteRequestType/RouteInstructionsRequest</a></small></i>
     * </blockquote>
     */
    public DetermineRouteRequest routeInstructionsRequest(RouteInstructionsRequestType routeInstructionsRequest) {
        this.routeInstructionsRequest = routeInstructionsRequest;
        return this;
    }

    /**
     * Route geometry request. Optional.
     *
     * @see #routeGeometryRequest(RouteGeometryRequestType)
     */
    public RouteGeometryRequestType getRouteGeometryRequest() {
        return routeGeometryRequest;
    }

    /**
     * <blockquote><q>
     * Request parameters for route geometry.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=127">; OGC 07-074 OpenLS Core, DetermineRouteRequestType/RouteGeometryRequest</a></small></i>
     * </blockquote>
     */
    public DetermineRouteRequest routeGeometryRequest(RouteGeometryRequestType routeGeometryRequest) {
        this.routeGeometryRequest = routeGeometryRequest;
        return this;
    }

    /**
     * Route map (rendered image) request. Optional.
     *
     * @see #routeMapRequest(RouteMapRequestType)
     */
    public RouteMapRequestType getRouteMapRequest() {
        return routeMapRequest;
    }

    /**
     * <blockquote><q>
     * Defines the request parameters for route maps.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=130">; OGC 07-074 OpenLS Core, RouteMapRequestType</a></small></i>
     * </blockquote>
     */
    public DetermineRouteRequest routeMapRequest(RouteMapRequestType routeMapRequest) {
        this.routeMapRequest = routeMapRequest;
        return this;
    }

    /**
     * Whether the server should return a reusable handle for this route. Optional.
     *
     * @see #provideRouteHandle(Boolean)
     */
    public Boolean getProvideRouteHandle() {
        return provideRouteHandle;
    }

    /**
     * <blockquote><q>
     * Requests the return of a route handle.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=127">; OGC 07-074 OpenLS Core, DetermineRouteRequestType/provideRouteHandle</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Default value: false.</li>
     * </ul>
     */
    public DetermineRouteRequest provideRouteHandle(Boolean provideRouteHandle) {
        this.provideRouteHandle = provideRouteHandle;
        return this;
    }

    /**
     * Unit used for reported distances. Optional.
     *
     * @see #distanceUnit(DistanceUnitType)
     */
    public DistanceUnitType getDistanceUnit() {
        return distanceUnit;
    }

    /**
     * <blockquote><q>
     * Specifies the unit for measuring distance.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=127">; OGC 07-074 OpenLS Core, DetermineRouteRequestType/distanceUnit</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Default: M.</li>
     * </ul>
     */
    public DetermineRouteRequest distanceUnit(DistanceUnitType distanceUnit) {
        this.distanceUnit = distanceUnit;
        return this;
    }
}

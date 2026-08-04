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
package org.geotoolkit.ogcapi.client.connectedsystems;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSubdeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSubsystem;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSystem;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSystemSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystem;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDeployments;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProcedures;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProperties;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSamplingFeatures;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSubdeployments;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSubsystems;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystem;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemDeployments;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemSamplingFeatures;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystems;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConnectedSystemsApiTest extends AbstractApiTest {

    // ------------------------------------------------------------------
    // Systems
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSystems(org.geotoolkit.ogcapi.request.connectedsystems.GetSystems) };
     * Request a list of System resources filtered on id, bbox, datetime, a WKT geometry with a
     * space, a keyword, parent, procedure, foi, observedProperty, controlledProperty, recursive
     * and limit.
     */
    @Test
    public void testGetSystems() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetSystems()
                    .id(List.of("sys1", "sys2"))
                    .bbox(bbox)
                    .datetime("2018-02-12T00:00:00Z")
                    .geom("POINT(1 2)")
                    .keyword(List.of("temperature", "humidity"))
                    .parent(List.of("parentSys"))
                    .procedure(List.of("proc1"))
                    .foi(List.of("foi1"))
                    .observedProperty(List.of("obsProp1"))
                    .controlledProperty(List.of("ctrlProp1"))
                    .recursive(true)
                    .limit(25);
            final var serviceRequest = api.getSystems(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems"
                    + "?id=sys1%2Csys2"
                    + "&bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&geom=POINT%281%202%29"
                    + "&q=temperature%2Chumidity"
                    + "&parent=parentSys"
                    + "&procedure=proc1"
                    + "&foi=foi1"
                    + "&observedProperty=obsProp1"
                    + "&controlledProperty=ctrlProp1"
                    + "&recursive=true"
                    + "&limit=25", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#createSystem(org.geotoolkit.ogcapi.request.connectedsystems.CreateSystem) };
     * Create a new System resource with a dry-run and a Prefer handling, then without either.
     */
    @Test
    public void testCreateSystem() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing content
            final var parameters = new CreateSystem();
            assertThrows(IllegalArgumentException.class, () -> api.createSystem(parameters));
        }

        { //check valid request : with dry-run and Prefer handling
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateSystem()
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createSystem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }

        { //check valid request : without dry-run nor Prefer handling
            final byte[] content = "{\"type\":\"Feature\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateSystem()
                    .content(content)
                    .contentType("application/geo+json");
            final var serviceRequest = api.createSystem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSystem(org.geotoolkit.ogcapi.request.connectedsystems.GetSystem) };
     * Request a single System resource with a datetime.
     */
    @Test
    public void testGetSystem() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new GetSystem();
            assertThrows(IllegalArgumentException.class, () -> api.getSystem(parameters));
        }

        { //check valid request
            final var parameters = new GetSystem()
                    .systemId("sys1")
                    .datetime("2019-01-01T00:00:00Z");
            final var serviceRequest = api.getSystem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1"
                    + "?datetime=2019-01-01T00%3A00%3A00Z", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#replaceSystem(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystem) };
     * Replace an existing System resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testReplaceSystem() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new ReplaceSystem();
            assertThrows(IllegalArgumentException.class, () -> api.replaceSystem(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceSystem()
                    .systemId("sys1")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(false)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceSystem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#deleteSystem(org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystem) };
     * Delete an existing System resource with a cascade.
     */
    @Test
    public void testDeleteSystem() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new DeleteSystem();
            assertThrows(IllegalArgumentException.class, () -> api.deleteSystem(parameters));
        }

        { //check valid request
            final var parameters = new DeleteSystem()
                    .systemId("sys1")
                    .cascade(true);
            final var serviceRequest = api.deleteSystem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1"
                    + "?cascade=true", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSubsystems(org.geotoolkit.ogcapi.request.connectedsystems.GetSubsystems) };
     * Request the subsystems of a System resource filtered on id and limit.
     */
    @Test
    public void testGetSubsystems() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new GetSubsystems();
            assertThrows(IllegalArgumentException.class, () -> api.getSubsystems(parameters));
        }

        { //check valid request
            final var parameters = new GetSubsystems()
                    .systemId("sys1")
                    .id(List.of("sub1"))
                    .limit(10);
            final var serviceRequest = api.getSubsystems(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/subsystems"
                    + "?id=sub1"
                    + "&limit=10", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#createSubsystem(org.geotoolkit.ogcapi.request.connectedsystems.CreateSubsystem) };
     * Create a new subsystem of a System resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testCreateSubsystem() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new CreateSubsystem();
            assertThrows(IllegalArgumentException.class, () -> api.createSubsystem(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateSubsystem()
                    .systemId("sys1")
                    .content(content)
                    .contentType("application/sml+json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createSubsystem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/subsystems"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/sml+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    // ------------------------------------------------------------------
    // Deployments
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsApi#getDeployments(org.geotoolkit.ogcapi.request.connectedsystems.GetDeployments) };
     * Request a list of Deployment resources filtered on id, datetime, parent, system and limit.
     */
    @Test
    public void testGetDeployments() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check valid request
            final var parameters = new GetDeployments()
                    .id(List.of("dep1", "dep2"))
                    .datetime("2020-05-01T12:30:00Z")
                    .parent(List.of("par1"))
                    .system(List.of("sysA"))
                    .limit(5);
            final var serviceRequest = api.getDeployments(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/deployments"
                    + "?id=dep1%2Cdep2"
                    + "&datetime=2020-05-01T12%3A30%3A00Z"
                    + "&parent=par1"
                    + "&system=sysA"
                    + "&limit=5", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#createDeployment(org.geotoolkit.ogcapi.request.connectedsystems.CreateDeployment) };
     * Create a new Deployment resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testCreateDeployment() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing content
            final var parameters = new CreateDeployment();
            assertThrows(IllegalArgumentException.class, () -> api.createDeployment(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateDeployment()
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(true)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.createDeployment(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/deployments"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getDeployment(org.geotoolkit.ogcapi.request.connectedsystems.GetDeployment) };
     * Request a single Deployment resource.
     */
    @Test
    public void testGetDeployment() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing deploymentId
            final var parameters = new GetDeployment();
            assertThrows(IllegalArgumentException.class, () -> api.getDeployment(parameters));
        }

        { //check valid request
            final var parameters = new GetDeployment()
                    .deploymentId("dep1");
            final var serviceRequest = api.getDeployment(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/deployments/dep1", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#replaceDeployment(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDeployment) };
     * Replace an existing Deployment resource with a dry-run and no Prefer handling.
     */
    @Test
    public void testReplaceDeployment() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing deploymentId
            final var parameters = new ReplaceDeployment();
            assertThrows(IllegalArgumentException.class, () -> api.replaceDeployment(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceDeployment()
                    .deploymentId("dep1")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(false);
            final var serviceRequest = api.replaceDeployment(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/deployments/dep1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#deleteDeployment(org.geotoolkit.ogcapi.request.connectedsystems.DeleteDeployment) };
     * Delete an existing Deployment resource.
     */
    @Test
    public void testDeleteDeployment() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing deploymentId
            final var parameters = new DeleteDeployment();
            assertThrows(IllegalArgumentException.class, () -> api.deleteDeployment(parameters));
        }

        { //check valid request
            final var parameters = new DeleteDeployment()
                    .deploymentId("dep1");
            final var serviceRequest = api.deleteDeployment(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/deployments/dep1", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSubdeployments(org.geotoolkit.ogcapi.request.connectedsystems.GetSubdeployments) };
     * Request the subdeployments of a Deployment resource filtered on recursive, id and limit.
     */
    @Test
    public void testGetSubdeployments() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing deploymentId
            final var parameters = new GetSubdeployments();
            assertThrows(IllegalArgumentException.class, () -> api.getSubdeployments(parameters));
        }

        { //check valid request
            final var parameters = new GetSubdeployments()
                    .deploymentId("dep1")
                    .recursive(true)
                    .id(List.of("sub1"))
                    .limit(20);
            final var serviceRequest = api.getSubdeployments(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/deployments/dep1/subdeployments"
                    + "?recursive=true"
                    + "&id=sub1"
                    + "&limit=20", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#createSubdeployment(org.geotoolkit.ogcapi.request.connectedsystems.CreateSubdeployment) };
     * Create a new subdeployment of a Deployment resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testCreateSubdeployment() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing deploymentId
            final var parameters = new CreateSubdeployment();
            assertThrows(IllegalArgumentException.class, () -> api.createSubdeployment(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateSubdeployment()
                    .deploymentId("dep1")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createSubdeployment(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/deployments/dep1/subdeployments"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSystemDeployments(org.geotoolkit.ogcapi.request.connectedsystems.GetSystemDeployments) };
     * Request the deployments of a System resource filtered on id, foi and limit.
     */
    @Test
    public void testGetSystemDeployments() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new GetSystemDeployments();
            assertThrows(IllegalArgumentException.class, () -> api.getSystemDeployments(parameters));
        }

        { //check valid request
            final var parameters = new GetSystemDeployments()
                    .systemId("sys1")
                    .id(List.of("dep1"))
                    .foi(List.of("foiA"))
                    .limit(15);
            final var serviceRequest = api.getSystemDeployments(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/deployments"
                    + "?id=dep1"
                    + "&foi=foiA"
                    + "&limit=15", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    // ------------------------------------------------------------------
    // Procedures
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsApi#getProcedures(org.geotoolkit.ogcapi.request.connectedsystems.GetProcedures) };
     * Request a list of Procedure resources filtered on id, datetime, a keyword with a space,
     * observedProperty, controlledProperty and limit.
     */
    @Test
    public void testGetProcedures() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check valid request
            final var parameters = new GetProcedures()
                    .id(List.of("proc1", "proc2"))
                    .datetime("2021-06-15T08:00:00Z")
                    .keyword(List.of("calibration routine"))
                    .observedProperty(List.of("op1"))
                    .controlledProperty(List.of("cp1"))
                    .limit(30);
            final var serviceRequest = api.getProcedures(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/procedures"
                    + "?id=proc1%2Cproc2"
                    + "&datetime=2021-06-15T08%3A00%3A00Z"
                    + "&q=calibration%20routine"
                    + "&observedProperty=op1"
                    + "&controlledProperty=cp1"
                    + "&limit=30", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#createProcedure(org.geotoolkit.ogcapi.request.connectedsystems.CreateProcedure) };
     * Create a new Procedure resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testCreateProcedure() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing content
            final var parameters = new CreateProcedure();
            assertThrows(IllegalArgumentException.class, () -> api.createProcedure(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateProcedure()
                    .content(content)
                    .contentType("application/sml+json")
                    .dryRun(true)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.createProcedure(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/procedures"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/sml+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getProcedure(org.geotoolkit.ogcapi.request.connectedsystems.GetProcedure) };
     * Request a single Procedure resource.
     */
    @Test
    public void testGetProcedure() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing procedureId
            final var parameters = new GetProcedure();
            assertThrows(IllegalArgumentException.class, () -> api.getProcedure(parameters));
        }

        { //check valid request
            final var parameters = new GetProcedure()
                    .procedureId("proc1");
            final var serviceRequest = api.getProcedure(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/procedures/proc1", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#replaceProcedure(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceProcedure) };
     * Replace an existing Procedure resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testReplaceProcedure() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing procedureId
            final var parameters = new ReplaceProcedure();
            assertThrows(IllegalArgumentException.class, () -> api.replaceProcedure(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceProcedure()
                    .procedureId("proc1")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(false)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.replaceProcedure(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/procedures/proc1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#deleteProcedure(org.geotoolkit.ogcapi.request.connectedsystems.DeleteProcedure) };
     * Delete an existing Procedure resource.
     */
    @Test
    public void testDeleteProcedure() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing procedureId
            final var parameters = new DeleteProcedure();
            assertThrows(IllegalArgumentException.class, () -> api.deleteProcedure(parameters));
        }

        { //check valid request
            final var parameters = new DeleteProcedure()
                    .procedureId("proc1");
            final var serviceRequest = api.deleteProcedure(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/procedures/proc1", request.uri().toString());
        }
    }

    // ------------------------------------------------------------------
    // Sampling Features
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSamplingFeatures(org.geotoolkit.ogcapi.request.connectedsystems.GetSamplingFeatures) };
     * Request a list of Sampling Feature resources filtered on id, bbox and limit.
     */
    @Test
    public void testGetSamplingFeatures() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{1.0, 2.0}, new double[]{3.0, 4.0});
            final var parameters = new GetSamplingFeatures()
                    .id(List.of("feat1"))
                    .bbox(bbox)
                    .limit(8);
            final var serviceRequest = api.getSamplingFeatures(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/samplingFeatures"
                    + "?id=feat1"
                    + "&bbox=1.0%2C2.0%2C3.0%2C4.0"
                    + "&limit=8", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSamplingFeature(org.geotoolkit.ogcapi.request.connectedsystems.GetSamplingFeature) };
     * Request a single Sampling Feature resource.
     */
    @Test
    public void testGetSamplingFeature() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing featureId
            final var parameters = new GetSamplingFeature();
            assertThrows(IllegalArgumentException.class, () -> api.getSamplingFeature(parameters));
        }

        { //check valid request
            final var parameters = new GetSamplingFeature()
                    .featureId("feat1");
            final var serviceRequest = api.getSamplingFeature(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/samplingFeatures/feat1", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#replaceSamplingFeature(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSamplingFeature) };
     * Replace an existing Sampling Feature resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testReplaceSamplingFeature() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing featureId
            final var parameters = new ReplaceSamplingFeature();
            assertThrows(IllegalArgumentException.class, () -> api.replaceSamplingFeature(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceSamplingFeature()
                    .featureId("feat1")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(true)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceSamplingFeature(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/samplingFeatures/feat1"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#deleteSamplingFeature(org.geotoolkit.ogcapi.request.connectedsystems.DeleteSamplingFeature) };
     * Delete an existing Sampling Feature resource.
     */
    @Test
    public void testDeleteSamplingFeature() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing featureId
            final var parameters = new DeleteSamplingFeature();
            assertThrows(IllegalArgumentException.class, () -> api.deleteSamplingFeature(parameters));
        }

        { //check valid request
            final var parameters = new DeleteSamplingFeature()
                    .featureId("feat1");
            final var serviceRequest = api.deleteSamplingFeature(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/samplingFeatures/feat1", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getSystemSamplingFeatures(org.geotoolkit.ogcapi.request.connectedsystems.GetSystemSamplingFeatures) };
     * Request the sampling features of a System resource filtered on id, foi and limit.
     */
    @Test
    public void testGetSystemSamplingFeatures() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new GetSystemSamplingFeatures();
            assertThrows(IllegalArgumentException.class, () -> api.getSystemSamplingFeatures(parameters));
        }

        { //check valid request
            final var parameters = new GetSystemSamplingFeatures()
                    .systemId("sys1")
                    .id(List.of("feat1"))
                    .foi(List.of("foiA"))
                    .limit(12);
            final var serviceRequest = api.getSystemSamplingFeatures(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/samplingFeatures"
                    + "?id=feat1"
                    + "&foi=foiA"
                    + "&limit=12", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/sml+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#createSystemSamplingFeature(org.geotoolkit.ogcapi.request.connectedsystems.CreateSystemSamplingFeature) };
     * Create a new sampling feature associated with a System resource with a dry-run and a Prefer
     * handling.
     */
    @Test
    public void testCreateSystemSamplingFeature() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new CreateSystemSamplingFeature();
            assertThrows(IllegalArgumentException.class, () -> api.createSystemSamplingFeature(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateSystemSamplingFeature()
                    .systemId("sys1")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createSystemSamplingFeature(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/samplingFeatures"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsApi#getProperties(org.geotoolkit.ogcapi.request.connectedsystems.GetProperties) };
     * Request a list of Property Definition resources filtered on id, baseProperty, objectType
     * and limit.
     */
    @Test
    public void testGetProperties() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check valid request
            final var parameters = new GetProperties()
                    .id(List.of("prop1"))
                    .baseProperty(List.of("base1"))
                    .objectType(List.of("obj1"))
                    .limit(6);
            final var serviceRequest = api.getProperties(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/properties"
                    + "?id=prop1"
                    + "&baseProperty=base1"
                    + "&objectType=obj1"
                    + "&limit=6", request.uri().toString());
            assertEquals(Optional.of("application/sml+json, application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#createProperty(org.geotoolkit.ogcapi.request.connectedsystems.CreateProperty) };
     * Create a new Property Definition resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testCreateProperty() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing content
            final var parameters = new CreateProperty();
            assertThrows(IllegalArgumentException.class, () -> api.createProperty(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateProperty()
                    .content(content)
                    .contentType("application/sml+json")
                    .dryRun(true)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.createProperty(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/properties"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/sml+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#getProperty(org.geotoolkit.ogcapi.request.connectedsystems.GetProperty) };
     * Request a single Property Definition resource.
     */
    @Test
    public void testGetProperty() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing propId
            final var parameters = new GetProperty();
            assertThrows(IllegalArgumentException.class, () -> api.getProperty(parameters));
        }

        { //check valid request
            final var parameters = new GetProperty()
                    .propId("prop1");
            final var serviceRequest = api.getProperty(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/properties/prop1", request.uri().toString());
            assertEquals(Optional.of("application/sml+json, application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#replaceProperty(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceProperty) };
     * Replace an existing Property Definition resource with a dry-run and a Prefer handling.
     */
    @Test
    public void testReplaceProperty() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing propId
            final var parameters = new ReplaceProperty();
            assertThrows(IllegalArgumentException.class, () -> api.replaceProperty(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceProperty()
                    .propId("prop1")
                    .content(content)
                    .contentType("application/sml+json")
                    .dryRun(false)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.replaceProperty(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/properties/prop1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/sml+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsApi#deleteProperty(org.geotoolkit.ogcapi.request.connectedsystems.DeleteProperty) };
     * Delete an existing Property Definition resource.
     */
    @Test
    public void testDeleteProperty() {
        final ConnectedSystemsApi api = new ConnectedSystemsApi(newConfig());

        { //check incorrect request : missing propId
            final var parameters = new DeleteProperty();
            assertThrows(IllegalArgumentException.class, () -> api.deleteProperty(parameters));
        }

        { //check valid request
            final var parameters = new DeleteProperty()
                    .propId("prop1");
            final var serviceRequest = api.deleteProperty(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/properties/prop1", request.uri().toString());
        }
    }

}

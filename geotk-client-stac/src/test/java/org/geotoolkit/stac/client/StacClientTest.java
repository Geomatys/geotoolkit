package org.geotoolkit.stac.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Item;
import org.geotoolkit.stac.dto.ItemCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link StacClient}.
 */
public class StacClientTest {

    @Mock
    private HttpClient httpClient;

    private StacClient stacClient;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        stacClient = new StacClient(httpClient, new DefaultDownloadURIExtractor());
    }

    @Test
    public void testSearchItemsSuccess() throws Exception {
        // Prepare mock response
        Item item = new Item();
        item.setId("test-item-1");
        ItemCollection collection = new ItemCollection();
        List<Item> features = new ArrayList<>();
        features.add(item);
        collection.setFeatures(features);
        
        String jsonResponse = mapper.writeValueAsString(collection);
        
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        List<Item> result = stacClient.searchItems("https://example.com/stac", "test-collection", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-item-1", result.get(0).getId());
    }

    @Test
    public void testGetDownloadUrl() {
        Item item = new Item();
        item.setId("test-item-download");
        
        Asset dataAsset = new Asset();
        dataAsset.setHref("https://example.com/data.nc");
        dataAsset.setRoles(Collections.singletonList("data"));
        
        Map<String, Asset> assets = new HashMap<>();
        assets.put("data", dataAsset);
        item.setAssets(assets);
        
        URI downloadUrl = stacClient.getDownloadURI(item);
        assertNotNull(downloadUrl);
        assertEquals("https://example.com/data.nc", downloadUrl.toString());
    }

    @Test
    public void testDownloadFile() throws Exception {
        HttpResponse<java.io.InputStream> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(new ByteArrayInputStream("fake netcdf data".getBytes()));
        
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Path tempDir = Files.createTempDirectory("stac-test");
        URI fileUri = URI.create("https://example.com/test_tile.nc");

        Path downloaded = stacClient.downloadFile(fileUri, tempDir);

        assertTrue(Files.exists(downloaded));
        assertEquals("test_tile.nc", downloaded.getFileName().toString());
        assertEquals("fake netcdf data", Files.readString(downloaded));
        Files.delete(downloaded);
        Files.delete(tempDir);
    }
}

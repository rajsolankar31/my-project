package com.productintel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthCheckReturnsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void searchProductsReturnsAllWhenQueryEmpty() throws Exception {
        mockMvc.perform(get("/api/products/search"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
            .andExpect(jsonPath("$[0].name").isNotEmpty());
    }

    @Test
    void searchProductsFindsMatchingProduct() throws Exception {
        mockMvc.perform(get("/api/products/search").param("q", "Sony"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].brand").value("Sony"))
            .andExpect(jsonPath("$[0].model").value("WH-1000XM5"));
    }

    @Test
    void searchProductsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/products/search").param("q", "macbook"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].name", containsString("MacBook Pro")));
    }

    @Test
    void getProductByIdReturnsCompleteDetailsAndVariants() throws Exception {
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.brand").isNotEmpty())
            .andExpect(jsonPath("$.variants", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$.variants[0].attributes").isMap());
    }

    @Test
    void getProductByInvalidIdReturns404() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getProductPricesReturnsTimelineAndListings() throws Exception {
        mockMvc.perform(get("/api/products/1/prices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(1))
            .andExpect(jsonPath("$.summary.lowestObserved").isNumber())
            .andExpect(jsonPath("$.summary.averageObserved").isNumber())
            .andExpect(jsonPath("$.summary.pricePosition").isString())
            .andExpect(jsonPath("$.timeline", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$.listings", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$.listings[0].sourceName").isNotEmpty())
            .andExpect(jsonPath("$.listings[0].price").isNumber());
    }

    @Test
    void resolveProductUrlMatchesAmazonListing() throws Exception {
        String jsonPayload = "{\"url\": \"https://www.amazon.in/dp/B09XS7JWHH\"}";

        mockMvc.perform(post("/api/products/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resolved").value(true))
            .andExpect(jsonPath("$.sourceName").value("Amazon India"))
            .andExpect(jsonPath("$.externalId").value("B09XS7JWHH"))
            .andExpect(jsonPath("$.product.id").isNumber())
            .andExpect(jsonPath("$.product.name", containsString("Sony")))
            .andExpect(jsonPath("$.prices.summary.currentObserved").isNumber());
    }

    @Test
    void resolveProductUrlRejectsUnsupportedDomain() throws Exception {
        String jsonPayload = "{\"url\": \"https://unsupported-unknown-shop.com/product/123\"}";

        mockMvc.perform(post("/api/products/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resolved").value(false))
            .andExpect(jsonPath("$.message", containsString("Unsupported source")));
    }
}

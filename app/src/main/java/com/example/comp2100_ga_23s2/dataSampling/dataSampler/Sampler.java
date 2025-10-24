package com.example.comp2100_ga_23s2.dataSampling.dataSampler;

import org.json.JSONException;

import java.io.IOException;

/**
 * Strategy, provide the blueprint for the samplers
 * @author Gia Minh Nguyen - u7556893
 */
public interface Sampler {
    void sampling() throws IOException;
}

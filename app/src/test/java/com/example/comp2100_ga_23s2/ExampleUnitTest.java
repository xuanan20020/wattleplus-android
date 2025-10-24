package com.example.comp2100_ga_23s2;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.comp2100_ga_23s2.dataSampling.dataSampler.CourseSampler;

import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAddingToFirebase() throws IOException {
        CourseSampler cs = new CourseSampler();
        cs.sampling();
    }
}
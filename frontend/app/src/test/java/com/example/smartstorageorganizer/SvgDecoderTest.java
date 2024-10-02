package com.example.smartstorageorganizer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.bumptech.glide.load.engine.Resource;
import com.caverock.androidsvg.SVG;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(RobolectricTestRunner.class)
public class SvgDecoderTest {

    private SvgDecoder svgDecoder;

    @Before
    public void setUp() {
        svgDecoder = new SvgDecoder();
    }

    @Test
    public void testDecode_ValidSvgStream_ReturnsSvgResource() throws IOException {
        // Sample valid SVG data
        String svgData = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"><circle cx=\"50\" cy=\"50\" r=\"40\"/></svg>";
        InputStream inputStream = new ByteArrayInputStream(svgData.getBytes());

        // Decode the input stream using SvgDecoder
        Resource<SVG> resource = svgDecoder.decode(inputStream, 100, 100, null);

        // Assert that the result is not null and the SVG resource is valid
        assertNotNull(resource);
        assertNotNull(resource.get());
    }


    @Test
    public void testHandles_AlwaysReturnsTrue() {
        // Create a mock InputStream
        InputStream inputStream = Mockito.mock(InputStream.class);

        // Assert that handles() always returns true
        assertTrue(svgDecoder.handles(inputStream, null));
    }
}


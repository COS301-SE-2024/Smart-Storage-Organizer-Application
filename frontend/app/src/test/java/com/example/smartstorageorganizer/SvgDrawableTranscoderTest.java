package com.example.smartstorageorganizer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SvgDrawableTranscoderTest {

    private SvgDrawableTranscoder svgDrawableTranscoder;

    @Before
    public void setUp() {
        svgDrawableTranscoder = new SvgDrawableTranscoder();
    }

    @Test
    public void testTranscode_ValidSvg_ReturnsPictureDrawable() {
        // Mock the SVG object
        SVG mockSvg = Mockito.mock(SVG.class);
        Picture mockPicture = Mockito.mock(Picture.class);

        // When renderToPicture is called, return a mock Picture
        when(mockSvg.renderToPicture()).thenReturn(mockPicture);

        // Wrap the mock SVG in a SimpleResource
        Resource<SVG> svgResource = new SimpleResource<>(mockSvg);

        // Transcode the SVG resource
        Resource<PictureDrawable> result = svgDrawableTranscoder.transcode(svgResource, null);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.get() instanceof PictureDrawable);

        // Verify that renderToPicture was called on the SVG object
        verify(mockSvg).renderToPicture();
    }

    @Test
    public void testTranscode_NullSvg_ReturnsNull() {
        // Passing a null SVG resource should return null
        Resource<SVG> svgResource = Mockito.mock(Resource.class);

        // Mock the get() method to return null when called
        when(svgResource.get()).thenReturn(null);

        // Transcode the SVG resource
        Resource<PictureDrawable> result = svgDrawableTranscoder.transcode(svgResource, null);

        // Verify that the result is null
        assertNull(result);
    }

}


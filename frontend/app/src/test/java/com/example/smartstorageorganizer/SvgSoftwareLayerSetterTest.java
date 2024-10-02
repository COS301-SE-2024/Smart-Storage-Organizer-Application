package com.example.smartstorageorganizer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Choose appropriate API level
public class SvgSoftwareLayerSetterTest {

    private ImageView mockImageView;
    private SvgSoftwareLayerSetter svgSoftwareLayerSetter;

    @Before
    public void setUp() {
        // Create a mock ImageView
        mockImageView = Mockito.mock(ImageView.class);
        // Initialize the SvgSoftwareLayerSetter with the mock ImageView
        svgSoftwareLayerSetter = new SvgSoftwareLayerSetter(mockImageView);
    }

    @Test
    public void testSetResource_SetsLayerTypeAndDrawable() {
        // Create a mock PictureDrawable
        PictureDrawable mockDrawable = Mockito.mock(PictureDrawable.class);

        // Call setResource with the mock drawable
        svgSoftwareLayerSetter.setResource(mockDrawable);

        // Verify that the ImageView's layer type is set to LAYER_TYPE_SOFTWARE
        verify(mockImageView, times(1)).setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);

        // Verify that the ImageView's drawable is set to the mock PictureDrawable
        verify(mockImageView, times(1)).setImageDrawable(mockDrawable);
    }

    @Test
    public void testSetResource_WithNullDrawable() {
        // Call setResource with a null drawable
        svgSoftwareLayerSetter.setResource(null);

        // Verify that the ImageView's layer type is still set to LAYER_TYPE_SOFTWARE
        verify(mockImageView, times(1)).setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);

        // Verify that the ImageView's drawable is set to null
        verify(mockImageView, times(1)).setImageDrawable(null);
    }
}

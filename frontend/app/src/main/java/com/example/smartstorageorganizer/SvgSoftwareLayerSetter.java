package com.example.smartstorageorganizer;

import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;
import com.bumptech.glide.request.target.ImageViewTarget;

public class SvgSoftwareLayerSetter extends ImageViewTarget<PictureDrawable> {

    public SvgSoftwareLayerSetter(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(PictureDrawable resource) {
        view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
        view.setImageDrawable(resource);
    }
}


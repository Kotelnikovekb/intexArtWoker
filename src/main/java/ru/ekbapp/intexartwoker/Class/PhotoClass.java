package ru.ekbapp.intexartwoker.Class;

import android.graphics.Bitmap;

import java.io.Serializable;

public class PhotoClass  implements Serializable {

    public final String patch;
    public final Bitmap bitmap;

    public PhotoClass(String patch, Bitmap bitmap) {
        this.patch = patch;
        this.bitmap = bitmap;
    }

    public String getPatch() {
        return patch;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}

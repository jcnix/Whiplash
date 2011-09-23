package org.cjones.facebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Friend
{
    private String name;
    private Bitmap photo;

    public Friend(String name, byte[] photo)
    {
        this.name = name;
        this.photo = BitmapFactory.decodeByteArray(photo, 0, photo.length);
    }

    public String getName()
    {
        return name;
    }

    public Bitmap getPhoto()
    {
        return photo;
    }

    public String toString()
    {
        return name;
    }
}


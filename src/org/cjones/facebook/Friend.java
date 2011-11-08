package org.cjones.facebook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class Friend
{
    private String name;
    private byte[] photo_bytes;
    private Bitmap photo;
    private Uri uri;

    public Friend(String name, byte[] photo, Uri uri)
    {
        this.name = name;
        this.photo_bytes = photo;
        this.photo = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        this.uri = uri;
    }

    public void updateContact(Activity activity)
    {
        ContactManager.setPhoto(uri, photo_bytes, activity);
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


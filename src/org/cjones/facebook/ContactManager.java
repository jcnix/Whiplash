package org.cjones.facebook;

import android.content.Context;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.net.Uri;

public class ContactManager
{
    public static void setPhoto(Uri personUri, byte[] photo, Context context)
    {
        ContentValues values = new ContentValues(); 
        int photoRow = -1; 
        String where = ContactsContract.Data.RAW_CONTACT_ID + " == " + 
            ContentUris.parseId(personUri) + " AND " + Data.MIMETYPE + "=='" + 
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'"; 
        Cursor cursor = context.getContentResolver().query 
            (ContactsContract.Data.CONTENT_URI, null, where, null, null); 
        int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID); 
        if(cursor.moveToFirst())
        { 
            photoRow = cursor.getInt(idIdx); 
        } 
        cursor.close(); 
        values.put(ContactsContract.Data.RAW_CONTACT_ID, 
            ContentUris.parseId(personUri)); 
        values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1); 
        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photo); 
        values.put(ContactsContract.Data.MIMETYPE, 
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE); 
        if(photoRow >= 0)
        { 
            context.getContentResolver().update 
                (ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data._ID 
                + " = " + photoRow, null); 
        } else { 
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values); 
        } 
    }
}


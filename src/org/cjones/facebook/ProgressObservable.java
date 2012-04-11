package org.cjones.facebook;

import java.util.Observable;

public class ProgressObservable extends Observable
{
    private static ProgressObservable po;

    protected ProgressObservable()
    {
    }

    public static ProgressObservable getInstance()
    {
        if(po == null)
        {
            po = new ProgressObservable();
        }
        return po;
    }

    public void clear()
    {
        deleteObservers();
    }

    public void notify(Friend response)
    {
        setChanged();
        notifyObservers(response);
    }

    public void notify(Album response)
    {
        setChanged();
        notifyObservers(response);
    }
}


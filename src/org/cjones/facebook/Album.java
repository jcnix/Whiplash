package org.cjones.facebook;

public class Album
{
    private int id;
    private String name;

    public Album(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}


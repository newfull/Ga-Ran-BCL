package vn.bcl.garanbcl.model;

import java.io.Serializable;

public class Category implements Serializable
{
    public int id;
    public String name;
    public int resourceId;

    public Category(int id, String name, int resourceId)
    {
        this.id = id;
        this.name = name;
        this.resourceId = resourceId;
    }
}

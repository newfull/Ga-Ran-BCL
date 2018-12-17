package vn.bcl.garanbcl.model;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

//TODO: implement according to DB
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

package vn.bcl.garanbcl.model;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

import vn.bcl.garanbcl.R;

//TODO: implement according to DB
public class Category implements Serializable
{
    public int id = 0;
    public String name = "";
    public String url = "";

    public Category()
    {
        this.id = 0;
        this.name = "";
        this.url = "";
    }

    public Category(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Category(int id, String name, String url)
    {
        this.id = id;
        this.name = name;
        this.url = url;
    }
}

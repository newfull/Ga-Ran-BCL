package vn.bcl.garanbcl.model;

import java.io.Serializable;

//TODO: implement according to DB
public class Item implements Serializable
{
    public int id = 0;
    public int categoryId = 0;
    public int subCategoryId = 0;
    public String name = "";
    public double unitPrice = 0;
    public String url = "";

    public Item()
    {
        this.id = 0;
        this.categoryId = 0;
        this.subCategoryId = 0;
        this.name = "";
        this.unitPrice = 0;
        this.url = "";
    }

    public Item(int id, int categoryId, int subCategoryId, String name, double unitPrice, String url)
    {
        this.id = id;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.url = url;
    }
}

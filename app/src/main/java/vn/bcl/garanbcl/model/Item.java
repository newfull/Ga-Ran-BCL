package vn.bcl.garanbcl.model;

import java.io.Serializable;

//TODO: implement according to DB
public class Item implements Serializable
{
    public int id;
    public int categoryId;
    public int subCategoryId;
    public String name;
    public double unitPrice;
    public String url;

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

package vn.bcl.garanbcl.model;

import java.io.Serializable;

//TODO: implement according to DB
public class SubCategory implements Serializable
{
    public int id = 0;
    public int categoryId = 0;
    public String name = "";
    public boolean isSelected = false;
    public boolean isExpanded = true;

    public SubCategory()
    {
        this.id = 0;
        this.categoryId = 0;
        this.name = "";
    }

    public SubCategory(int id, int categoryId, String name)
    {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
    }

    public SubCategory(SubCategory subCategory)
    {
        this.id = subCategory.id;
        this.categoryId = subCategory.categoryId;
        this.name = subCategory.name;
        this.isSelected = subCategory.isSelected;
    }

    public void setIsExpanded()
    {
        isExpanded= (!isExpanded);
    }
}

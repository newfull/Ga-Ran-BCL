package vn.bcl.garanbcl.model;

import java.io.Serializable;

//TODO: implement according to DB
public class SubCategory implements Serializable
{
    public int id;
    public int categoryId;
    public String name;
    public boolean isSelected = false;
    public boolean isExpanded = true;

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

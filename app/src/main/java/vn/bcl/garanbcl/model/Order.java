package vn.bcl.garanbcl.model;

import static java.lang.Double.parseDouble;

public class Order
{
    private Item item;
    private int quantity;
    private String extendedPrice;

    public Order(){
        this.item = null;
        this.quantity = 0;
        this.extendedPrice = "0.0";
    }

    public Order(Item item, int quantity)
    {
        this.item = item;
        this.quantity = quantity;
        this.extendedPrice = String.valueOf(item.unitPrice * quantity);
    }

    public Item getItem(){
        return this.item;
    }
    public void setItem(Item item){
        this.item = item;
    }

    public int getQuantity(){
        return this.quantity;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
        this.extendedPrice = String.valueOf(quantity*this.item.unitPrice);
    }

    public String getPrice(){
        return this.extendedPrice;
    }

    public void addQuantity(int quantity){
        this.quantity += quantity;
        this.extendedPrice = String.valueOf(parseDouble(this.extendedPrice) + quantity*this.item.unitPrice);
    }

}

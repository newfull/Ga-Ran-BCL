package vn.bcl.garanbcl.model;

import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;

public class Receipt {
    private String id;
    private String date;
    private String address;
    private String total;
    private List<Order> items;

    public Receipt(){
        this.date = null;
        this.address = "";
        this.total = "0.0";
        this.items = null;
    }

    public Receipt(String date, String address, String total, List<Order> items)
    {
        this.date = date;
        this.address = address;
        this.total = total;
        this.items = items;
    }

    public String getDate(){
        return this.date;
    }

    public String getAddress(){
        return this.address;
    }

    public String getTotal(){
        return this.total;
    }

    public List<Order> getItems(){
        return this.items;
    }

    public String getId(){
        return this.id;
    }
}

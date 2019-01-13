package vn.bcl.garanbcl.model;

import java.util.List;

public class News {
    private String id;
    private String date;
    private String content;
    private String title;
    private String url;

    public News(){
        this.id = null;
        this.date = null;
        this.title = null;
        this.content = null;
        this.url = null;
    }

    public News(String id, String date, String content, String title, String url){
        this.id = id;
        this.date = date;
        this.content = content;
        this.title = title;
        this.url = url;
    }

    public String getDate(){
        return this.date;
    }

    public String getTitle(){
        return this.title;
    }

    public String getContent(){
        return this.content;
    }

    public String getUrl(){ return this.url; }

    public String getId(){
        return this.id;
    }
}

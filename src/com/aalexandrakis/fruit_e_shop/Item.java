package com.aalexandrakis.fruit_e_shop;

public class Item {
    private int categoryId;
    private int ItemCode;
    private String ItemDescr;
    private String ItemMm;
    private Float ItemPrice;
    private Float ItemQuantity;
    private Float ItemSummary;
    private int display;
    private int lastUpdate;

    public Item(){

    }
    public Item(Integer C, String D, String M, Float P, Float Q, Float S ) {
    	this.ItemCode = C;
    	this.ItemDescr = D;
    	this.ItemMm = M;
    	this.ItemPrice = P;
    	this.ItemQuantity = Q;
    	this.ItemSummary = S;
    }
    
    public Integer getItemCode(){
 	   return ItemCode;
    }
    public void setItemCode(Integer ItemCode){
 	   this.ItemCode=ItemCode;
    }
    public String getItemDescr(){
  	   return ItemDescr;
     }
    public void setItemDescr(String ItemDescr){
  	   this.ItemDescr=ItemDescr;
     }
    public String getItemMm(){
       return ItemMm;
       }
    public void setItemMm(String ItemMm){
       this.ItemMm=ItemMm;
    }
    public Float getItemPrice(){
       return ItemPrice;
        }
    public void setItemPrice(Float ItemPrice){
       this.ItemPrice=ItemPrice;
    }
    public Float getItemQuantity(){
        return ItemQuantity;
         }
    public void setItemQuantity(Float ItemQuantity){
       this.ItemQuantity=ItemQuantity;
       this.setItemSummary(getItemPrice() * getItemQuantity());
    }
    public Float getItemSummary(){
        return ItemSummary;
         }
    public void setItemSummary(Float ItemSummary){
       this.ItemSummary=ItemSummary;
    }
    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }
    public int getCategoryId(){
        return this.categoryId;
    }
    public int getDisplay() {
        return display;
    }
    public void setDisplay(int display) {
        this.display = display;
    }
    public void setItemCode(int itemCode) {
        ItemCode = itemCode;
    }
    public int getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

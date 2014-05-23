package com.aalexandrakis.fruit_e_shop;

public class Item {
    private Integer ItemCode;
    private String ItemDescr;
    private String ItemMm;
    private Float ItemPrice;
    private Float ItemQuantity;
    private Float ItemSummary;
    
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
    }
    public Float getItemSummary(){
        return ItemSummary;
         }
    public void setItemSummary(Float ItemSummary){
       this.ItemSummary=ItemSummary;
    }    
}

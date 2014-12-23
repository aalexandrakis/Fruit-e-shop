package com.aalexandrakis.fruit_e_shop;

public class Order {
    private Integer OrderId;
    private String OrderDate;
    private Float OrderAmount;
    private String OrderStatus;

    public Order(){

    }
    public Order(Integer I, String D, Float A, String S) {
    	this.OrderId = I;
    	this.OrderDate = D;
    	this.OrderAmount = A;
    	this.OrderStatus = S;
    }
    
    public Integer getOrderId(){
 	   return OrderId;
    }
    public void setOrderId(Integer OrderId){
 	   this.OrderId=OrderId;
    }
    public String getOrderDate(){
  	   return OrderDate;
     }
    public void setOrderDate(String OrderDate){
  	   this.OrderDate=OrderDate;
     }
    public Float getOrderAmount(){
       return OrderAmount;
       }
    public void setOrderAmount(Float OrderAmount){
       this.OrderAmount=OrderAmount;
    }
    public String getOrderStatus(){
       return OrderStatus;
        }
    public void setOrderStatus(String OrderStatus){
       this.OrderStatus=OrderStatus;
    }
        
}

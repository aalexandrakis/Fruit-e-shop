package com.aalexandrakis.fruit_e_shop;

public class Category {
    private Integer CategoryCode;
    private String CategoryDescr;
    
    public Category(Integer C, String D) {
    	this.CategoryCode = C;
    	this.CategoryDescr = D;
    	
    }
    
    public Integer getCategoryCode(){
 	   return CategoryCode;
    }
    public void setCategoryCode(Integer CategoryCode){
 	   this.CategoryCode=CategoryCode;
    }
    public String getCategoryDescr(){
  	   return CategoryDescr;
     }
    public void setCategoryDescr(String CategoryDescr){
  	   this.CategoryDescr=CategoryDescr;
     }
   }

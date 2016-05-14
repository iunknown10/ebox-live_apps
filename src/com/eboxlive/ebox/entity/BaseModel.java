package com.eboxlive.ebox.entity;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

public class BaseModel implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	// 设置为主键,自增
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public int id;
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
}

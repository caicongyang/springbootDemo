package com.caicongyang.stock.domain;

import java.io.Serializable;

/**
 * @author caicongyang
 * @version id: User, v 0.1 17/10/24 下午8:01 caicongyang1 Exp $$
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 3325474521628784682L;

    private Long              userId;
    private String            name;
    private Integer           age;
    private Adress            adress;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }
}

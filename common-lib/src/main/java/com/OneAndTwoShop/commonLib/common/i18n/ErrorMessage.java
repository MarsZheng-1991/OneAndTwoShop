package com.OneAndTwoShop.commonLib.common.i18n;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "error_message")
public class ErrorMessage {

    @Id
    private String key;

    private String zh;
    private String en;

    public String getKey() { return key; }
    public String getZh() { return zh; }
    public String getEn() { return en; }
}
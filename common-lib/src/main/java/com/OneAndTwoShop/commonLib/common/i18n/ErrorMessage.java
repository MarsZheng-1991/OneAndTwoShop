package com.OneAndTwoShop.commonLib.common.i18n;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "error_message")
@Data
public class ErrorMessage {

    @Id
    private String key;

    private String zh;
    private String en;
}
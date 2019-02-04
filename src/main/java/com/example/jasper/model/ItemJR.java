package com.example.jasper.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class ItemJR {
    private String itemName;
    private BigDecimal itemPrice;
}

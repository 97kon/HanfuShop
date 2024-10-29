package com.clothrent.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class SearchGoods {
    String beginTime;
    String endTime;
    Long categoryId;
    Long userId;
    String name;//物品名称

}

package com.clothrent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PageDTO {
    @ApiModelProperty(value = "页码",required = true,dataType = "integer",example ="1")
    Integer pageNum=1;//页码
    @ApiModelProperty(value = "每页大小",required = true,dataType = "integer",example ="10")
    Integer pageSize=10000;//页大小
    @ApiModelProperty(value = "其他字段模糊查询",required = false,dataType = "string",example ="jane")
    String vagueParam;//模糊查询
    String isAsc="desc";//asc or desc
    String orderByColumn="id";//根据某个字段进行排序
    String beginTime;
    String endTime;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getVagueParam() {
        return vagueParam;
    }

    public void setVagueParam(String vagueParam) {
        this.vagueParam = vagueParam;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {

        this.orderByColumn = orderByColumn.isEmpty()?"id":orderByColumn;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}

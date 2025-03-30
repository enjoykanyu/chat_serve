package com.kanyu.chat.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;
    private Integer code;
    public static Result ok(){
        return new Result(true, null, null, null,200);
    }
    public static Result ok(Object data){
        return new Result(true, null, data, null,200);
    }
    public static Result ok(List<?> data, Long total,Integer code){
        return new Result(true, null, data, total,code);
    }
    public static Result fail(String errorMsg,Integer code){
        return new Result(false, errorMsg, null, null,code);
    }
}

package com.clothrent;

import com.clothrent.common.HttpUtils;
import com.clothrent.common.RegexUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class ClothRentApplicationTest {



    @Test
    public void testContains(){
        Map<String,String> headermap=new HashMap<>();
        headermap.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");
        String s = HttpUtils.httpGet("https://club.jd.com/comment/productCommentSummaries.action?referenceIds=100005907830",headermap);
        System.out.println(s);
    }


    @Test
    public void testMatchStr(){
        String source="1.5万";
        String regex="[\\u4e00-\\u9fa5]+";
        String goal = RegexUtil.parseGoalFromStr(source, regex);
        String[] split = source.split(goal);
        if(goal.equals("万")){
            System.out.println("***********************"+ Float.parseFloat(split[0])*10000);
        }else if(goal.equals("亿")){
            System.out.println("***********************"+ Float.parseFloat(split[0])*100000000);
        }
    }
}

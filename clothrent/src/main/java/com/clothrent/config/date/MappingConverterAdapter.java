package com.clothrent.config.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 解决日期格式转化-Sting
 * -- LocalDateTime
 * -- LocalDate
 */
@Configuration
public class MappingConverterAdapter {
    private static  final Logger logger=LoggerFactory.getLogger(MappingConverterAdapter.class);

    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    //String转换为LocalDateTime
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConvert() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                if(StringUtils.isEmpty(source)){
                    return null;
                }
                DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
                LocalDateTime dateTime = null;
                try {
                    //2020-01-01 00:00:00
                    dateTime = LocalDateTime.parse(dateTimeText(source), df);
                } catch (Exception e) {
                   logger.error(e.getMessage(),e);
                }
                return dateTime;
            }
        };
    }
    //LocalDateTime转换为String
    @Bean
    public Converter<LocalDateTime, String> localDateTimeToString() {
        return new Converter<LocalDateTime, String>() {
            @Override
            public String convert(LocalDateTime localDateTime) {
                if(localDateTime==null){
                    return null;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return formatter.format(localDateTime);
            }
        };
    }

    /**
     * 时间字符串格式化
     * @param source
     * @return
     */
    private static String dateTimeText(String source){
        //2020-01-01 00:00:00
        switch (source.length()){
            case 10:
                logger.debug("传过来的是日期格式：{}",source);
                source=source+" 00:00:00";
                break;
            case 13:
                logger.debug("传过来的是日期 小时格式：{}",source);
                source=source+":00:00";
                break;
            case 16:
                logger.debug("传过来的是日期 小时:分钟格式：{}",source);
                source=source+":00";
                break;
        }
        return source;
    }
    // 日期格式化器
    @Bean
    public Formatter<LocalDate> localDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale)  {
                return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            @Override
            public String print(LocalDate localDate, Locale locale) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return formatter.format(localDate);
            }
        };
    }
}

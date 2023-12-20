package io.github.xtools.bean.fliter.config.service;


import io.github.xtools.bean.fliter.config.service.impl.*;

import java.util.Date;

public enum FieldType {
    Boolean(Boolean.class, MappingBooleanService.class),
    //Char(Character.class, MappingCharService.class),
    Byte(Byte.class, MappingByteService.class),
    Short(Short.class, MappingShortService.class),
    Int(Integer.class, MappingIntegerService.class),
    Long(Long.class, MappingLongService.class),
    Float(Float.class, MappingFloatService.class),
    Double(Double.class, MappingDoubleService.class),

    String(String.class, MappingStringService.class),
    Date(Date.class, MappingDateService.class),
    BigDecimal(java.math.BigDecimal.class, MappingBigDecimalService.class),


    ;
    private final Class<?> type;
    private final Class<?> service;

    FieldType(Class<?> type, Class<?> service) {
        this.type = type;
        this.service = service;
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getService() {
        return service;
    }
}

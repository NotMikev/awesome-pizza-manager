package com.awesome.pizza.order.manager.mapper.purchase;

import org.mapstruct.Mapper;
import com.awesome.pizza.order.manager.entity.Purchase;
import com.awesome.pizza.order.manager.dto.purchase.PurchaseDto;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    
    Purchase toEntity(PurchaseDto dto);
    
    PurchaseDto toDto(Purchase entity);
}

package br.com.rafaellbarros.order.model.mapper;

import br.com.rafaellbarros.order.model.dto.OrderDTO;
import br.com.rafaellbarros.order.model.dto.ProductDTO;
import br.com.rafaellbarros.order.model.entity.Order;
import br.com.rafaellbarros.order.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "products.order", ignore = true)
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt() : java.time.LocalDateTime.now())")
    OrderDTO toDTO(Order entity);
    Order toEntity(OrderDTO dto);

    @Mapping(target = "products.order", ignore = true)
    List<OrderDTO> toDTO(List<Order> entities);

    @Mapping(target = "order", ignore = true)
    ProductDTO productToProductDTO(Product product);

    @Mapping(target = "order", ignore = true)
    Product productDTOToProduct(ProductDTO productDTO);

}

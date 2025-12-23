package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProductToWarehouse(NewProductInWarehouseRequest newProduct);

    BookedProductsDto checkAvailabilityForCart(ShoppingCartDto shoppingCartDto);

    void addProductQuantity(AddProductToWarehouseRequest addProductDto);

    AddressDto getWarehouseAddress();

    void ShippedToDelivery(ShippedToDeliveryRequest deliveryRequest);

    BookedProductsDto AssemblyToDelivery(AssemblyProductsForOrderRequest assemblyRequest);

    void returnProducts(Map<UUID, Integer> products);
}

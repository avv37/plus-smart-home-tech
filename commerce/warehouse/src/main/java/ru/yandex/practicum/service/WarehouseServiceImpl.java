package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exceptions.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exceptions.ProductNotFoundException;
import ru.yandex.practicum.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.model.WarehouseProductMapper;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseProductRepository warehouseRepository;
    private final WarehouseProductMapper warehouseMapper;
    private final OrderBookingRepository orderBookingRepository;
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    @Transactional
    public void addNewProductToWarehouse(NewProductInWarehouseRequest newProduct) {
        if (warehouseRepository.existsById(newProduct.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с таким описанием уже зарегистрирован на складе");
        }
        WarehouseProduct product = warehouseMapper.toModel(newProduct);
        warehouseRepository.save(product);
    }

    @Override
    public BookedProductsDto checkAvailabilityForCart(ShoppingCartDto shoppingCartDto) {
        log.info("Проверка достаточного количества товаров для корзины {}", shoppingCartDto.getShoppingCartId());
        Map<UUID, Integer> products = shoppingCartDto.getProducts();

        BookedProductsDto bookedProductsDto = checkAvailabilityForProductsMap(products);
        return bookedProductsDto;
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest addProductDto) {
        UUID productId = addProductDto.getProductId();
        WarehouseProduct product = checkIdExistsOrThrow(productId);
        product.setQuantity(product.getQuantity() + addProductDto.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return new AddressDto(
                CURRENT_ADDRESS, // country
                CURRENT_ADDRESS, // city
                CURRENT_ADDRESS, // street
                CURRENT_ADDRESS, // house
                CURRENT_ADDRESS  // flat
        );
    }

    @Override
    @Transactional
    public void ShippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        /*
        Метод должен обновить информацию о собранном заказе в базе данных склада:
        добавить в него идентификатор доставки, который вернул сервис доставки,
        присвоить идентификатор доставки во внутреннем хранилище собранных товаров заказа.
        Вызывается из сервиса доставки.
         */
        UUID orderId = deliveryRequest.getOrderId();
        OrderBooking orderBooking = orderBookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Бронирование для заказа " + orderId + " не найдено"));
        orderBooking.setDeliveryId(deliveryRequest.getDeliveryId());
        orderBookingRepository.save(orderBooking);
    }

    @Override
    @Transactional
    public void returnProducts(Map<UUID, Integer> products) {
        // метод принимает список товаров с количеством и увеличивает доступный остаток
        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer returnedQuantity = entry.getValue();

            WarehouseProduct product = warehouseRepository.findWithLockByProductId(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Продукт с ID " + productId + " не существует"));
            // увеличивает их доступный остаток
            product.setQuantity(product.getQuantity() + returnedQuantity);
        }
    }

    @Override
    @Transactional
    public BookedProductsDto AssemblyToDelivery(AssemblyProductsForOrderRequest assemblyRequest) {
        //Собрать товары к заказу для подготовки к отправке.
        Map<UUID, Integer> products = assemblyRequest.getProducts();
        // проверяется наличие заказанных товаров в нужном количестве
        BookedProductsDto bookedProductsDto = checkAvailabilityForProductsMap(products);

        UUID orderId = assemblyRequest.getOrderId();

        OrderBooking orderBooking = orderBookingRepository.findByOrderId(orderId)
                .orElseGet(() -> OrderBooking.builder().orderId(orderId).build());

        orderBooking.setDeliveryWeight(BigDecimal.valueOf(bookedProductsDto.getDeliveryWeight()));
        orderBooking.setDeliveryVolume(BigDecimal.valueOf(bookedProductsDto.getDeliveryVolume()));
        orderBooking.setFragile(bookedProductsDto.getFragile());
        orderBooking.getBookingProducts().clear();

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            WarehouseProduct product = warehouseRepository.findWithLockByProductId(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Продукт с ID " + productId + " не существует"));
            // уменьшается их доступный остаток
            product.setQuantity(product.getQuantity() - requestedQuantity);
            // Hibernate автоматически синхронизирует изменения при коммите транзакции не нужно делать warehouseRepository.save(product)
            // добавляем забронированные товары
            orderBooking.addBookingProduct(product, requestedQuantity);
        }
        orderBookingRepository.save(orderBooking);

        return bookedProductsDto;
    }

    private BookedProductsDto checkAvailabilityForProductsMap(Map<UUID, Integer> products) {
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        boolean isAnyFragile = false;

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            WarehouseProduct product = checkIdExistsOrThrow(productId);
            checkEnoughQuantityOrThrow(product, requestedQuantity);

            BigDecimal qty = new BigDecimal(requestedQuantity);
            totalWeight = totalWeight.add(product.getWeight().multiply(qty));
            BigDecimal volume = product.getWidth()
                    .multiply(product.getHeight())
                    .multiply(product.getDepth())
                    .multiply(qty);
            totalVolume = totalVolume.add(volume);
            if (product.getFragile()) {
                isAnyFragile = true;
            }
        }

        return new BookedProductsDto(totalWeight.doubleValue(), totalVolume.doubleValue(), isAnyFragile);

    }

    private WarehouseProduct checkIdExistsOrThrow(UUID productId) {
        log.info("Проверка существования productId");
        WarehouseProduct product = warehouseRepository.findById(productId).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("Продукт с ID " + productId + " не существует"));
        return product;
    }

    private void checkEnoughQuantityOrThrow(WarehouseProduct product, Integer requestedQuantity) {
        log.info("Проверка достаточного количества товара ");
        if (product.getQuantity() < requestedQuantity) {
            throw new ProductInShoppingCartLowQuantityInWarehouseException(
                    String.format("Не хватает товара %s. Требуется: %d, доступно: %d",
                            product.getProductId(), requestedQuantity, product.getQuantity()));
        }
    }

}

package ru.yandex.practicum.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import ru.yandex.practicum.model.WarehouseProduct;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WarehouseProduct> findWithLockByProductId(UUID id);
}

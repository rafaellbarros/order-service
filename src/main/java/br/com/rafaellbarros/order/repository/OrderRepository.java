package br.com.rafaellbarros.order.repository;


import br.com.rafaellbarros.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByExternalId(final String exteranlId);
}

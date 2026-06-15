package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreatePaymentRequest;
import com.jackson_api.JacksonApi.application.dto.response.PaymentResponse;
import com.jackson_api.JacksonApi.application.mapper.PaymentMapper;
import com.jackson_api.JacksonApi.domain.entity.Order;
import com.jackson_api.JacksonApi.domain.entity.OrderDetail;
import com.jackson_api.JacksonApi.domain.entity.Payment;
import com.jackson_api.JacksonApi.domain.enums.MovementType;
import com.jackson_api.JacksonApi.domain.enums.OrderStatus;
import com.jackson_api.JacksonApi.domain.enums.PaymentStatus;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.jackson_api.JacksonApi.domain.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InventoryMovementService inventoryMovementService;
    private final PaymentMapper paymentMapper;

    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(paymentMapper::toResponse);
    }

    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        return paymentMapper.toResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByOrderId(UUID orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        return paymentMapper.toResponseList(paymentRepository.findByOrder_Id(orderId));
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("La orden ya no esta pendiente");
        }

        if (!paymentRepository.findByOrder_Id(order.getId()).isEmpty()) {
            throw new RuntimeException("La orden ya tiene un pago registrado");
        }

        if (request.getAmount().compareTo(order.getTotal()) != 0) {
            throw new RuntimeException("El monto no coincide con el total de la orden");
        }

        Payment payment = paymentMapper.toCreate(request, order);

        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(request.getTransactionId());

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(UUID id, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        payment.setStatus(newStatus);

        if (newStatus == PaymentStatus.COMPLETED) {

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            for (OrderDetail detail : order.getOrderDetails()) {
                String motivo = "Venta - " + detail.getQuantity() + "x " + detail.getProductName()
                        + " (Orden #" + order.getOrderNumber() + ")";
                inventoryMovementService.recordMovement(
                        detail.getProduct(),
                        detail.getQuantity(),
                        MovementType.SALE,
                        motivo);
            }
        }

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }
}

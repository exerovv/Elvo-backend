package com.example.database.ordering.datasource

import com.example.database.ordering.dto.OrderDTO
import com.example.database.ordering.dto.UpdateOrderDTO
import com.example.database.ordering.response.OrderFullResponse
import com.example.database.ordering.response.OrderPaymentStatusResponse
import com.example.database.ordering.response.OrderShortResponse

interface OrderingDataSource {
    suspend fun getAllOrdersForUser(userId: Int): List<OrderShortResponse>
    suspend fun getOrderFullById(orderingId: Int): OrderFullResponse?
    suspend fun getOrderShortById(orderingId: Int): OrderShortResponse?
    suspend fun insertOrder(userId: Int, order: OrderDTO): Int
    suspend fun updateOrder(orderId: Int, updateOrderingDTO: UpdateOrderDTO): Boolean
    suspend fun makePayment(orderId: Int, paymentStatus: String): Boolean
    suspend fun getPaymentStatusesForArrivedOrders(currentStatusId: Int): List<OrderPaymentStatusResponse>
}
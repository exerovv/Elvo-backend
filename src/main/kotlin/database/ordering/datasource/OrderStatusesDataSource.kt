package com.example.database.ordering.datasource

import com.example.database.ordering.dto.OrderStatusDTO
import com.example.database.ordering.response.OrderStatusResponse

interface OrderStatusesDataSource {
    suspend fun insertNewStatus(newRecord: OrderStatusDTO)
    suspend fun getStatusHistory(orderId: Int): List<OrderStatusResponse>
}
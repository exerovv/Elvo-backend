package com.example.database.routing

import com.example.core.ErrorResponse
import com.example.database.ordering.datasource.OrderStatusesDataSource
import com.example.database.ordering.datasource.OrderingDataSource
import com.example.database.ordering.datasource.StatusesDataSource
import com.example.database.ordering.dto.OrderDTO
import com.example.database.ordering.dto.OrderStatusDTO
import com.example.database.ordering.dto.UpdateOrderDTO
import com.example.database.ordering.request.AddOrderRequest
import com.example.database.ordering.request.UpdateOrderRequest
import com.example.database.ordering.response.OrderFullInfoResponse
import com.example.database.ordering.utils.PaymentStatus
import com.example.database.recipient.RecipientDataSource
import com.example.database.token.getUserIdClaim
import com.example.utils.ErrorCode
import com.example.utils.OrderValidator
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock

fun Route.orderRouting(
    statusesDataSource: StatusesDataSource,
    orderStatusesDataSource: OrderStatusesDataSource,
    orderingDataSource: OrderingDataSource,
    recipientDataSource: RecipientDataSource
) {
    route("order") {
        get("list") {
            val userId = call.getUserIdClaim() ?: run {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }

            try {
                val orderList = orderingDataSource.getAllOrdersForUser(userId)
                call.respond(
                    HttpStatusCode.OK,
                    orderList
                )
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }
        }
        get("{id}") {
            val userId = call.getUserIdClaim() ?: run {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }

            val orderId = call.parameters["id"]?.toInt()

            if (orderId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@get
            }

            try {
                val order = orderingDataSource.getOrderFullById(orderId)

                if (order == null) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@get
                }

                val recipient = recipientDataSource.getRecipientById(userId, order.recipientId)

                if (recipient == null) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    OrderFullInfoResponse(
                        order, recipient
                    )
                )
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }
        }
        post("add") {
            val userId = call.getUserIdClaim() ?: run {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }

            val addRequest = call.receiveNullable<AddOrderRequest>()

            if (addRequest == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@post
            }

            if (!OrderValidator.validateAllFields(
                    addRequest.recipientId,
                    addRequest.orderName,
                    addRequest.trackNumber,
                    addRequest.ruDescription,
                    addRequest.chDescription,
                    addRequest.itemPrice
                )
            ) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_ORDER_DATA
                    )
                )
                return@post
            }

            if (OrderValidator.validateLink(addRequest.link)) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_LINK
                    )
                )
                return@post
            }

            val order = try {
                val status = statusesDataSource.getStatusByCode("CH_created")

                if (status == null) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_CREDENTIALS
                        )
                    )
                    return@post
                }
                val orderId = orderingDataSource.insertOrder(
                    userId,
                    OrderDTO(
                        addRequest.recipientId!!,
                        addRequest.orderName!!,
                        addRequest.trackNumber!!,
                        Clock.System.now(),
                        status.id,
                        status.globalStatus,
                        PaymentStatus.NOT_REQUIRED.toString(),
                        addRequest.ruDescription!!,
                        addRequest.chDescription!!,
                        addRequest.link!!,
                        addRequest.itemPrice!!
                    )
                )
                orderStatusesDataSource.insertNewStatus(OrderStatusDTO(orderId, status.id, Clock.System.now()))

                orderingDataSource.getOrderShortById(orderId)
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }

            if (order == null) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.ORDER_NOT_FOUND
                    )
                )
                return@post
            }

            call.respond(
                HttpStatusCode.OK,
                order
            )
        }
        get("history/{id}") {
            val orderId = call.parameters["id"]?.toInt()

            if (orderId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@get
            }

            val history = try {
                orderStatusesDataSource.getStatusHistory(orderId)
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }

            call.respond(
                HttpStatusCode.OK,
                history
            )
        }
        post("pay/{id}") {
            val orderId = call.parameters["id"]?.toInt()

            if (orderId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@post
            }

            try {
                val order = orderingDataSource.getOrderShortById(orderId)
                if (order == null) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.ORDER_NOT_FOUND
                        )
                    )
                    return@post
                }
                if (order.paymentStatus != PaymentStatus.REQUIRED_NOT_PAID.toString()) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.IMPOSSIBLE_TO_PAY
                        )
                    )
                    return@post
                }
                orderingDataSource.makePayment(orderId, PaymentStatus.REQUIRED_PAID.toString())
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }
            call.respond(
                HttpStatusCode.OK,
                PaymentStatus.REQUIRED_PAID.toString()
            )
        }

        put("update-status/{id}") {
            val orderId = call.parameters["id"]?.toInt()

            if (orderId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@put
            }

            val updateRequest = call.receiveNullable<UpdateOrderRequest>()

            if (updateRequest == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@put
            }

            try {
                val order = orderingDataSource.getOrderShortById(orderId)
                if (order == null) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.ORDER_NOT_FOUND
                        )
                    )
                    return@put
                }

                val weight = updateRequest.weight
                val totalPrice = updateRequest.totalPrice
                val status = updateRequest.status
                var paymentStatus: String? = null

                if (order.currentStatus == "CH_created") {
                    if (!OrderValidator.validateWeightAndPrice(weight, totalPrice)) {
                        call.respond(
                            HttpStatusCode.BadRequest, ErrorResponse(
                                errorCode = ErrorCode.INCORRECT_CREDENTIALS
                            )
                        )
                        return@put
                    }
                    paymentStatus = PaymentStatus.REQUIRED_NOT_PAID.toString()
                }

                val currentStatus = statusesDataSource.getStatusByCode(status)

                if (currentStatus == null) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@put
                }

                val updateStatusId = currentStatus.id + 1

                if (!OrderValidator.validateUpdateStatus(updateStatusId)) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.ORDER_ALREADY_DELIVERED
                        )
                    )
                    return@put
                }

                orderingDataSource.updateOrder(
                    orderId,
                    UpdateOrderDTO(
                        weight,
                        totalPrice,
                        updateStatusId,
                        paymentStatus
                    )
                )

                orderStatusesDataSource.insertNewStatus(OrderStatusDTO(orderId, updateStatusId, Clock.System.now()))
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@put
            }

            call.respond(
                HttpStatusCode.OK
            )
        }

        get("payment-status") {
            try {
                val status = statusesDataSource.getStatusByCode("CH_received")
                if (status == null) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@get
                }
                val statuses = orderingDataSource.getPaymentStatusesForArrivedOrders(status.id)
                call.respond(
                    HttpStatusCode.OK,
                    statuses
                )
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }
        }

//        sse("status-updates/{id}", serialize = { typeInfo, it ->
//            val serializer = Json.serializersModule.serializer(typeInfo.kotlinType!!)
//            Json.encodeToString(serializer, it)
//        }) {
//            val userId = call.getUserIdClaim() ?: run {
//                call.respond(
//                    HttpStatusCode.Conflict, ErrorResponse(
//                        errorCode = ErrorCode.SERVER_ERROR
//                    )
//                )
//                return@sse
//            }
//
//            try {
//                val order = orderingDataSource.getOrderShortById(orderId)
//                if (order == null){
//                    call.respond(
//                        HttpStatusCode.BadRequest, ErrorResponse(
//                            errorCode = ErrorCode.SERVER_ERROR
//                        )
//                    )
//                    return@sse
//                }
//                send(order)
//            }catch (_: Exception){
//                call.respond(
//                    HttpStatusCode.BadRequest, ErrorResponse(
//                        errorCode = ErrorCode.SERVER_ERROR
//                    )
//                )
//                return@sse
//            }
//
//        }
    }
}
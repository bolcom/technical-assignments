package com.bol.test.assignment.aggregator;

import com.bol.test.assignment.offer.Offer;
import com.bol.test.assignment.offer.OfferCondition;
import com.bol.test.assignment.offer.OfferService;
import com.bol.test.assignment.order.Order;
import com.bol.test.assignment.order.OrderService;
import com.bol.test.assignment.product.Product;
import com.bol.test.assignment.product.ProductService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregatorService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private OrderService orderService;
    private OfferService offerService;
    private ProductService productService;

    public AggregatorService(OrderService orderService, OfferService offerService, ProductService productService) {
        this.orderService = orderService;
        this.offerService = offerService;
        this.productService = productService;
    }

    public EnrichedOrder enrich(int sellerId) throws ExecutionException, InterruptedException {
        Order order = retrieveOrder(sellerId);
        Offer offer = retrieveOffer(order.getOfferId());

        Product product = retrieveProduct(order.getProductId());

        return combine(order, offer, product);
    }

    private Order retrieveOrder(int sellerId) {
        try {
            LOGGER.debug("retrieving order with sellerId: " + sellerId);
            return orderService.getOrder(sellerId);
        } catch (RuntimeException e) {
            LOGGER.error("Can't retrieve order with sellerId: " + sellerId);
            throw new RuntimeException("Invalid order!!!");
        }
    }

    private Offer retrieveOffer(int offerId) {
        try {
            LOGGER.debug("retrieving offer with offerId: " + offerId);
            return offerService.getOffer(offerId);
        } catch (RuntimeException e) {
            LOGGER.error("Can't retrieve offer with orderId: " + offerId);
            return new Offer(-1, OfferCondition.UNKNOWN);
        }
    }

    private Product retrieveProduct(int productId) {
        try {
            LOGGER.debug("retrieving product with productId: " + productId);
            return productService.getProduct(productId);
        } catch (RuntimeException e) {
            LOGGER.error("Can't retrieve product with productId: " + productId);
            return new Product(-1, null);
        }
    }

    private EnrichedOrder combine(Order order, Offer offer, Product product) {
        return new EnrichedOrder(order.getId(), offer.getId(), offer.getCondition(), product.getId(), product.getTitle());
    }
}

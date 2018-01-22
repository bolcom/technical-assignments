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

public class AggregatorService {

    private OrderService orderService;
    private OfferService offerService;
    private ProductService productService;

    public AggregatorService(OrderService orderService, OfferService offerService, ProductService productService) {
        this.orderService = orderService;
        this.offerService = offerService;
        this.productService = productService;
    }

    public EnrichedOrder enrich(int sellerId) throws ExecutionException, InterruptedException {
        Order order;
        order = orderService.getOrder(sellerId);
        Offer offer;
        try {
            offer = offerService.getOffer(order.getOfferId());
        } catch (RuntimeException e) {
            offer = new Offer(-1, OfferCondition.UNKNOWN);
        }
        Product product;
        try {
            product = productService.getProduct(order.getProductId());
        } catch (RuntimeException e) {
            product = new Product(-1, null);
        }
        return combine(order, offer, product);
    }

    private EnrichedOrder combine(Order order, Offer offer, Product product) {
        return new EnrichedOrder(order.getId(), offer.getId(), offer.getCondition(), product.getId(), product.getTitle());
    }
}

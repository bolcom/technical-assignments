package com.bol.test.assignment.aggregator;

import com.bol.test.assignment.offer.Offer;
import com.bol.test.assignment.offer.OfferCondition;
import com.bol.test.assignment.offer.OfferService;
import com.bol.test.assignment.order.Order;
import com.bol.test.assignment.order.OrderService;
import com.bol.test.assignment.product.Product;
import com.bol.test.assignment.product.ProductService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregatorService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final OrderService orderService;
    private final OfferService offerService;
    private final ProductService productService;

    public AggregatorService(OrderService orderService, OfferService offerService, ProductService productService) {
        this.orderService = orderService;
        this.offerService = offerService;
        this.productService = productService;
    }

    public EnrichedOrder enrich(int sellerId) throws ExecutionException, InterruptedException {
        return retrieveOrder(sellerId)
                .thenComposeAsync(new Function<Order, CompletionStage<EnrichedOrder>>() {
                    @Override
                    public CompletionStage<EnrichedOrder> apply(Order order) {
                        return retrieveOffer(order.getOfferId())
                                .thenCombineAsync(retrieveProduct(order.getProductId()), new BiFunction<Offer, Product, EnrichedOrder>() {
                                    @Override
                                    public EnrichedOrder apply(Offer offer, Product product) {
                                        return combine(order, offer, product);
                                    }
                                });
                    }
                }).join();
    }

    private CompletableFuture<Order> retrieveOrder(int sellerId) {
        return CompletableFuture.supplyAsync(new Supplier<Order>() {
            @Override
            public Order get() {
                LOGGER.debug("retrieving order with sellerId: " + sellerId);
                return orderService.getOrder(sellerId);
            }
        }, executorService)
                .exceptionally(new Function<Throwable, Order>() {
                    @Override
                    public Order apply(Throwable throwable) {
                        LOGGER.error("Can't retrieve order with sellerId: " + sellerId);
                        throw new RuntimeException("Invalid order!!!");
                    }
                });
    }

    private CompletableFuture<Offer> retrieveOffer(int offerId) {
        return CompletableFuture.supplyAsync(new Supplier<Offer>() {
            @Override
            public Offer get() {
                LOGGER.debug("retrieving offer with offerId: " + offerId);
                return offerService.getOffer(offerId);
            }
        }, executorService)
                .exceptionally(new Function<Throwable, Offer>() {
                    @Override
                    public Offer apply(Throwable throwable) {
                        LOGGER.error("Can't retrieve offer with orderId: " + offerId);
                        return new Offer(-1, OfferCondition.UNKNOWN);
                    }
                });
    }

    private CompletableFuture<Product> retrieveProduct(int productId) {
        return CompletableFuture.supplyAsync(new Supplier<Product>() {
            @Override
            public Product get() {
                LOGGER.debug("retrieving product with productId: " + productId);
                return productService.getProduct(productId);
            }
        }, executorService)
                .exceptionally(new Function<Throwable, Product>() {
                    @Override
                    public Product apply(Throwable throwable) {
                        LOGGER.error("Can't retrieve product with productId: " + productId);
                        return new Product(-1, null);
                    }
                });
    }

    private EnrichedOrder combine(Order order, Offer offer, Product product) {
        return new EnrichedOrder.EnrichedOrderBuilder()
                .id(order.getId())
                .offerId(offer.getId())
                .offerCondition(offer.getCondition())
                .productId(product.getId())
                .productTitle(product.getTitle())
                .build();
    }
}

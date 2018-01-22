package com.bol.test.assignment.aggregator;

import com.bol.test.assignment.offer.OfferCondition;

public class EnrichedOrder {

    private int id;
    private int offerId;
    private OfferCondition offerCondition;
    private int productId;
    private String productTitle;

    public EnrichedOrder(int id, int offerId, OfferCondition offerCondition, int productId, String productTitle) {
        this.id = id;
        this.offerId = offerId;
        this.offerCondition = offerCondition;
        this.productId = productId;
        this.productTitle = productTitle;
    }

    public int getId() {
        return id;
    }

    public int getOfferId() {
        return offerId;
    }

    public OfferCondition getOfferCondition() {
        return offerCondition;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public static class EnrichedOrderBuilder {

        private int id;
        private int offerId;
        private OfferCondition offerCondition;
        private int productId;
        private String productTitle;

        public EnrichedOrderBuilder() {
        }

        public EnrichedOrderBuilder id(int id) {
            this.id = id;
            return this;
        }

        public EnrichedOrderBuilder offerId(int offerId) {
            this.offerId = offerId;
            return this;
        }

        public EnrichedOrderBuilder offerCondition(OfferCondition offerCondition) {
            this.offerCondition = offerCondition;
            return this;
        }

        public EnrichedOrderBuilder productId(int productId) {
            this.productId = productId;
            return this;
        }

        public EnrichedOrderBuilder productTitle(String productTitle) {
            this.productTitle = productTitle;
            return this;
        }

        public EnrichedOrder build() {
            return new EnrichedOrder(this.id, this.offerId, this.offerCondition, this.productId, this.productTitle);
        }
    }
}

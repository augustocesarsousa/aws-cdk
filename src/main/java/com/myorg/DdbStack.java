package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;

public class DdbStack extends Stack {
    private final Table productEventDdb;

    public DdbStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public DdbStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        productEventDdb = Table.Builder.create(this, "ProductEventDb")
            .tableName("product-events")
            .billingMode(BillingMode.PROVISIONED)
            .readCapacity(1)
            .writeCapacity(1)
            .partitionKey(Attribute.builder()
                .name("pk")
                .type(AttributeType.STRING)
                .build())
            .sortKey(Attribute.builder()
                .name("pk")
                .type(AttributeType.STRING)
                .build())
            .timeToLiveAttribute("ttl")
            .removalPolicy(RemovalPolicy.DESTROY)
            .build();
    }

    public Table getProductEventDdb() {
        return productEventDdb;
    }

}

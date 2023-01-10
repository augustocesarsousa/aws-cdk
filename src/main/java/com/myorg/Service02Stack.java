package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.CpuUtilizationScalingProps;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.ScalableTaskCount;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class Service02Stack extends Stack {
    public Service02Stack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB01")
            .serviceName("Service-02")
            .cluster(cluster)
            .cpu(512)
            .memoryLimitMiB(1024)
            .desiredCount(2)
            .listenerPort(9090)
            .taskImageOptions(
                ApplicationLoadBalancedTaskImageOptions.builder()
                    .containerName("aws_project02")
                    .image(ContainerImage.fromRegistry("acs03/aws_project02:1.0.0"))
                    .containerPort(9090)
                    .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, "Service02LogGroup")
                            .logGroupName("Service02")
                            .removalPolicy(RemovalPolicy.DESTROY)
                            .build())
                        .streamPrefix("Sergice02")
                        .build()))
                    .build())
            .publicLoadBalancer(true)
            .build();

        service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
            .path("/actuator/health")
            .port("9090")
            .healthyHttpCodes("200")
            .build());

        ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(EnableScalingProps.builder()
            .minCapacity(2)
            .maxCapacity(4)
            .build());

        scalableTaskCount.scaleOnCpuUtilization("Service02AutoScaling", CpuUtilizationScalingProps.builder()
            .targetUtilizationPercent(50)
            .scaleInCooldown(Duration.seconds(60))
            .scaleOutCooldown(Duration.seconds(60))
            .build());
    }
}

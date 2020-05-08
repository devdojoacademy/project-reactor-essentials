package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
/*
  Reactive Streams
  1. Asynchronous
  2. Non-blocking
  3. Backpressure
  Publisher <- (subscribe) Subscriber
  Subscription is created
  Publisher (onSubscribe with the subscription) -> Subscriber
  Subscription <- (request N) Subscriber
  Publisher -> (onNext) Subscriber
  until:
  1. Publisher sends all the objects requested.
  2. Publisher sends all the objects it has. (onComplete) subscriber and subscription will be canceled
  3. There is an error. (onError) -> subscriber and subscription will be canceled
 */
public class MonoTest {

    @Test
    public void monoSubscriber() {
        String name = "William Suane";
        Mono<String> mono = Mono.just(name)
            .log();

        mono.subscribe();
        log.info("--------------------------");
        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "William Suane";
        Mono<String> mono = Mono.just(name)
            .log();

        mono.subscribe(s -> log.info("Value {}", s));
        log.info("--------------------------");

        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerError() {
        String name = "William Suane";
        Mono<String> mono = Mono.just(name)
            .map(s -> {
                throw new RuntimeException("Testing mono with error");
            });

        mono.subscribe(s -> log.info("Name {}", s), s -> log.error("Something bad happened"));
        mono.subscribe(s -> log.info("Name {}", s), Throwable::printStackTrace);

        log.info("--------------------------");

        StepVerifier.create(mono)
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String name = "William Suane";
        Mono<String> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Value {}", s),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!"));

        log.info("--------------------------");

        StepVerifier.create(mono)
            .expectNext(name.toUpperCase())
            .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerSubscription() {
        String name = "William Suane";
        Mono<String> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Value {}", s),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!")
            , Subscription::cancel);

        log.info("--------------------------");

        StepVerifier.create(mono)
            .expectNext(name.toUpperCase())
            .verifyComplete();
    }
}

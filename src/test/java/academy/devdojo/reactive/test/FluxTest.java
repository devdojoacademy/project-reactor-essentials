package academy.devdojo.reactive.test;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxTest {

    @Test
    public void fluxSubscriber() {
        Flux<String> fluxString = Flux.just("William", "Suane", "DevDojo", "Academy")
            .log();

        StepVerifier.create(fluxString)
            .expectNext("William", "Suane", "DevDojo", "Academy")
            .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbers() {
        Flux<Integer> flux = Flux.range(1, 5)
            .log();

        flux.subscribe(i -> log.info("Number {}", i));

        log.info("-----------------------------------");
        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }

    @Test
    public void fluxSubscriberFromList() {
        Flux<Integer> flux = Flux.fromIterable(List.of(1,2,3,4,5))
            .log();

        flux.subscribe(i -> log.info("Number {}", i));

        log.info("-----------------------------------");
        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersError() {
        Flux<Integer> flux = Flux.range(1,5)
            .log()
            .map(i -> {
                if(i == 4) {
                    throw new IndexOutOfBoundsException("index error");
                }
                return i;
            });

        flux.subscribe(i -> log.info("Number {}", i), Throwable::printStackTrace,
            () -> log.info("DONE!"), subscription -> subscription.request(3));

        log.info("-----------------------------------");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3)
            .expectError(IndexOutOfBoundsException.class)
            .verify();
    }

    @Test
    public void fluxSubscriberNumbersUglyBackpressure() {
        Flux<Integer> flux = Flux.range(1,10)
            .log();

        flux.subscribe(new Subscriber<>() {
            private int count = 0;
            private Subscription subscription;
            private int requestCount = 2;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;
                if(count >= requestCount){
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        log.info("-----------------------------------");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3,4,5,6,7,8,9,10)
            .verifyComplete();
    }
}

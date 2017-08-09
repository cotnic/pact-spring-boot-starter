package io.pivotal.microservices.pact;

import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.pivotal.microservices.pact.consumer.Application;
import io.pivotal.microservices.pact.consumer.Foo;
import io.pivotal.microservices.pact.consumer.ConsumerPort;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
@WebAppConfiguration
public class ConsumerPortTest {

    @Rule
    @Autowired
    public PactProviderRuleMk2 rule;
    // public PactProviderRuleMk2 rule = new PactProviderRuleMk2("Foo_Provider", "localhost", 8080, this);

    @Pact(provider="Foo_Provider", consumer="Foo_Consumer")
    public RequestResponsePact createFragment(PactDslWithProvider builder) {
        return builder.uponReceiving("a request for Foos")
                .path("/foos")
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body("[{\"value\":42}, {\"value\":100}]")
                .toPact();
    }

    @Test
    @PactVerification("Foo_Provider")
    public void runTest() {
        ConsumerPort consumerPort = new ConsumerPort(rule.getConfig().url());
        assertThat("Response JSON body has to have values 42 and 100!",
                consumerPort.foos(),
                is(Arrays.asList(new Foo(42), new Foo(100))));
    }
}
package uk.co.asepstrath.bank;

import io.jooby.JoobyTest;
import io.jooby.MockRouter;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
class IntegrationTest {
    /*
    Integration tests should be here
    Example can be found in example/IntegrationTest.java
     */
    /**
     * Test the welcome route.
     * @since 1.0.0
     */
    @Test
    void test_home_200() {
        MockRouter router = new MockRouter(new App());
        router.get("/", rsp -> assertEquals(StatusCode.OK, rsp.getStatusCode()));
    }
}

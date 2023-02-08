package uk.co.asepstrath.bank;

import io.jooby.MockRouter;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
    /**
     * Test the welcome route.
     * @throws Exception if something goes wrong.
     * @since 1.0.0
     */
    @Test
    public void test_home_200() {
        MockRouter router = new MockRouter(new App());
        router.get("/", rsp -> {
            assertEquals(StatusCode.OK, rsp.getStatusCode());
        });
    }
}

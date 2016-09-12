package controllers;

import org.junit.Test;
import static org.junit.Assert.*;

public class mainControllerTest {

    @Test
    public void TestMatePassTest() {
        boolean abc = true;
        assertTrue(abc);
    }

    @Test
    public void TestMateFailTest() {
        boolean xyz = true;
        // change xyz = false
        assertFalse(xyz);
    }
}

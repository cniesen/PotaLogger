package com.niesens.potalogger.enumerations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BandTest {

    @Test
    public void test_toString() {
        assertEquals("2190m", Band.BAND_2190m.toString());
        assertEquals("1.25m", Band.BAND_1_25m.toString());
        assertEquals("2.5mm", Band.BAND_2_5mm.toString());
    }

    @Test
    public void test_valueOf() {
        assertEquals(null, Band.ofFrequency("0.13569"));
        assertEquals(Band.BAND_2190m, Band.ofFrequency("0.1357"));
        assertEquals(Band.BAND_2190m, Band.ofFrequency("0.136"));
        assertEquals(Band.BAND_2190m, Band.ofFrequency("0.1378"));
        assertEquals(null, Band.ofFrequency("0.13781"));
    }

}
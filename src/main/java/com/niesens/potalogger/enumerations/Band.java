package com.niesens.potalogger.enumerations;

public enum Band {
    BAND_2190m("2190m", 0.1357f, 0.1378f),
    BAND_630m("630m", 0.472f, 0.479f),
    BAND_560m("560m", 0.501f, 0.504f),
    BAND_160m("160m", 1.8f, 2f),
    BAND_80m("80m", 3.5f, 4),
    BAND_60m("60m", 5.06f, 5.45f),
    BAND_40m("40m", 7, 7.3f),
    BAND_30m("30m", 10.1f, 10.15f),
    BAND_20m("20m", 14, 14.35f),
    BAND_17m("17m", 18.068f, 18.168f),
    BAND_15m("15m", 21, 21.45f),
    BAND_12m("12m", 24.89f, 24.99f),
    BAND_10m("10m", 28, 29.7f),
    BAND_6m("6m", 50, 54),
    BAND_4m("4m", 70, 71),
    BAND_2m("2m", 144, 148),
    BAND_1_25m("1.25m", 222, 225),
    BAND_70cm("70cm", 420, 450),
    BAND_33cm("33cm", 902, 928),
    BAND_23cm("23cm", 1240, 1300),
    BAND_13cm("13cm", 2300, 2450),
    BAND_9cm("9cm", 3300, 3500),
    BAND_6cm("6cm", 5650, 5925),
    BAND_3cm("3cm", 10000, 10500),
    BAND_1_25cm("1.25cm", 24000, 24250),
    BAND_6mm("6mm", 47000, 47200),
    BAND_4mm("4mm", 75500, 81000),
    BAND_2_5mm("2.5mm", 119980, 120020),
    BAND_2mm("2mm", 142000, 149000),
    BAND_1mm("1mm", 241000, 250000);


    private final String band;
    private final float minFrequency;
    private final float maxFrequency;

    Band(final String band, float minFrequency, float maxFrequency) {
        this.band = band;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
    }

    @Override
    public String toString() {
        return band;
    }

    public static Band ofFrequency(String frequency) {
        float parsedFrequency = Float.parseFloat(frequency);
        for (Band band : Band.values()) {
            if ((band.minFrequency <= parsedFrequency) && (band.maxFrequency >= parsedFrequency)) {
                return band;
            }
        }
        return null;
    }
}

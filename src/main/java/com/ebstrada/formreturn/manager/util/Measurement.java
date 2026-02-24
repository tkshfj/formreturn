package com.ebstrada.formreturn.manager.util;

public class Measurement {

    public static final int PIXELS = 1;
    public static final int MILLIMETERS = 2;
    public static final int CENTIMETERS = 3;
    public static final int INCHES = 4;

    public static final double DPI = 72.0d;
    public static final double MILLIMETERS_IN_AN_INCH = 25.4d;

    private int from;
    private int to;

    public Measurement() {
        this.from = PIXELS;
        this.to = MILLIMETERS;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public double convert(double input) {
        // formula:
        // pixels = (mm * dpi) / 25.4
        // inches = pixels / 72
        return switch (this.from) {
            case PIXELS -> switch (this.to) {
                case PIXELS -> input;
                case MILLIMETERS -> (input * MILLIMETERS_IN_AN_INCH) / DPI;
                case CENTIMETERS -> ((input * MILLIMETERS_IN_AN_INCH) / DPI) / 10.0d;
                case INCHES -> input / DPI;
                default -> 0.0d;
            };
            case MILLIMETERS -> switch (this.to) {
                case PIXELS -> (input * DPI) / MILLIMETERS_IN_AN_INCH;
                case MILLIMETERS -> input;
                case CENTIMETERS -> input / 10.0d;
                case INCHES -> input / MILLIMETERS_IN_AN_INCH;
                default -> 0.0d;
            };
            case CENTIMETERS -> switch (this.to) {
                case PIXELS -> ((input * 10.0d) * DPI) / MILLIMETERS_IN_AN_INCH;
                case MILLIMETERS -> input * 10.0d;
                case CENTIMETERS -> input;
                case INCHES -> (input * 10.0d) / MILLIMETERS_IN_AN_INCH;
                default -> 0.0d;
            };
            case INCHES -> switch (this.to) {
                case PIXELS -> input * DPI;
                case MILLIMETERS -> input * MILLIMETERS_IN_AN_INCH;
                case CENTIMETERS -> (input * MILLIMETERS_IN_AN_INCH) / 10.0d;
                case INCHES -> input;
                default -> 0.0d;
            };
            default -> 0.0d;
        };
    }

}

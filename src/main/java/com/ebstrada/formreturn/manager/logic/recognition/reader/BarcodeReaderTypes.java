package com.ebstrada.formreturn.manager.logic.recognition.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Reader;
import com.google.zxing.oned.Code128Reader;
import com.google.zxing.oned.Code39Reader;
import com.google.zxing.oned.EAN13Reader;
import com.google.zxing.oned.EAN8Reader;
import com.google.zxing.oned.ITFReader;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.google.zxing.oned.UPCAReader;
import com.google.zxing.oned.UPCEReader;
import com.google.zxing.qrcode.QRCodeReader;

public class BarcodeReaderTypes {

    public static final int AUTO_DETECT = 0;

    public static final int CODE_128 = 1;

    public static final int CODE_39 = 2;

    public static final int EAN_13 = 3;

    public static final int EAN_8 = 4;

    public static final int ITF = 5;

    public static final int UPC_A = 6;

    public static final int UPC_E = 7;

    public static final int UPC_EAN = 8;

    public static final int QR_CODE = 9;

    private static List<String> barcodeTypes = new ArrayList<String>();

    public String getBarcodeDescription(int barcodeType) {
        return switch (barcodeType) {
            case AUTO_DETECT -> "Auto Detect";
            case CODE_128 -> "Code 128";
            case CODE_39 -> "Code 39";
            case EAN_13 -> "EAN 13";
            case EAN_8 -> "EAN 8";
            case ITF -> "ITF";
            case UPC_A -> "UPC-A";
            case UPC_E -> "UPC-E";
            case UPC_EAN -> "UPC-EAN";
            case QR_CODE -> "QR Code";
            default -> "";
        };
    }

    public static List<String> getBarcodeTypes() {
        if (barcodeTypes.size() <= 0) {
            resetBarcodeTypes();
        }

        List<String> descriptions = new ArrayList<String>();

        for (int i = 0; i < barcodeTypes.size(); i++) {
            descriptions.add(barcodeTypes.get(i));
        }
        return descriptions;
    }

    public static void resetBarcodeTypes() {
        barcodeTypes = new ArrayList<String>();
        barcodeTypes.add("Auto Detect");
        barcodeTypes.add("Code 128");
        barcodeTypes.add("Code 39");
        barcodeTypes.add("EAN 13");
        barcodeTypes.add("EAN 8");
        barcodeTypes.add("ITF");
        barcodeTypes.add("UPC-A");
        barcodeTypes.add("UPC-E");
        barcodeTypes.add("UPC-EAN");
        barcodeTypes.add("QR Code");
    }

    public static Reader getDelegate(int barcodeType) {
        return switch (barcodeType) {
            case CODE_128 -> new Code128Reader();
            case CODE_39 -> new Code39Reader();
            case EAN_13 -> new EAN13Reader();
            case EAN_8 -> new EAN8Reader();
            case ITF -> new ITFReader();
            case UPC_A -> new UPCAReader();
            case UPC_E -> new UPCEReader();
            case UPC_EAN -> new UPCEReader();
            case QR_CODE -> new QRCodeReader();
            default -> {
                var hints = new HashMap<DecodeHintType, Object>();
                var possibleFormats = new Vector<BarcodeFormat>();
                possibleFormats.add(BarcodeFormat.CODE_128);
                possibleFormats.add(BarcodeFormat.CODE_39);
                possibleFormats.add(BarcodeFormat.ITF);
                possibleFormats.add(BarcodeFormat.EAN_13);
                possibleFormats.add(BarcodeFormat.EAN_8);
                possibleFormats.add(BarcodeFormat.UPC_A);
                possibleFormats.add(BarcodeFormat.UPC_E);
                possibleFormats.add(BarcodeFormat.QR_CODE);
                hints.put(DecodeHintType.POSSIBLE_FORMATS, possibleFormats);
                yield new MultiFormatOneDReader(hints);
            }
        };
    }

}

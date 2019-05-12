package com.example.botmaster.desteganography;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;
import com.example.botmaster.Utility;

public class Decode {

    private static final String TAG = Decode.class.getName();
    //start and end message constants
    private static final String END_MESSAGE_COSTANT = "#!@";
    private static final String START_MESSAGE_COSTANT = "@!#";
    private static final int[] binary = {16, 8, 0};
    private static final byte[] andByte = {(byte) 0xC0, 0x30, 0x0C, 0x03};
    private static final int[] toShift = {6, 4, 2, 0};


    private static void decodeMessage(byte[] byte_pixel_array, int image_columns,
                                      int image_rows, MessageDecodingStatus messageDecodingStatus) {

        //encrypted message
        Vector<Byte> byte_encrypted_message = new Vector<>();

        int shiftIndex = 4;

        byte tmp = 0x00;


        for (byte aByte_pixel_array : byte_pixel_array) {


            //get last two bits from byte_pixel_array
            tmp = (byte) (tmp | ((aByte_pixel_array << toShift[shiftIndex
                    % toShift.length]) & andByte[shiftIndex++ % toShift.length]));

            if (shiftIndex % toShift.length == 0) {
                //adding temp byte value
                byte_encrypted_message.addElement(tmp);


                //converting byte value to string
                byte[] nonso = {byte_encrypted_message.elementAt(byte_encrypted_message.size() - 1)};
                String str = new String(nonso, Charset.forName("ISO-8859-1"));

                if (messageDecodingStatus.getMessage().endsWith(END_MESSAGE_COSTANT)) {

                    Log.i("TEST", "Decoding ended");

                    //fixing ISO-8859-1 decoding
                    byte[] temp = new byte[byte_encrypted_message.size()];

                    for (int index = 0; index < temp.length; index++)
                        temp[index] = byte_encrypted_message.get(index);


                    String stra = new String(temp, Charset.forName("ISO-8859-1"));


                    messageDecodingStatus.setMessage(stra.substring(0, stra.length() - 1));
                    //end fixing

                    messageDecodingStatus.setEnded();

                    break;
                } else {
                    //just add the decoded message to the original message
                    messageDecodingStatus.setMessage(messageDecodingStatus.getMessage() + str);

                    //If there was no message there and only start and end message constant was there
                    if (messageDecodingStatus.getMessage().length() == START_MESSAGE_COSTANT.length()
                            && !START_MESSAGE_COSTANT.equals(messageDecodingStatus.getMessage())) {

                        messageDecodingStatus.setMessage("");
                        messageDecodingStatus.setEnded();

                        break;
                    }
                }

                tmp = 0x00;
            }

        }

        if (!Utility.isStringEmpty(messageDecodingStatus.getMessage()))
            //removing start and end constants form message

            try {
                messageDecodingStatus.setMessage(messageDecodingStatus.getMessage().substring(START_MESSAGE_COSTANT.length(), messageDecodingStatus.getMessage()
                        .length()
                        - END_MESSAGE_COSTANT.length()));
            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    /**
     * This method takes the list of encoded chunk images and decodes it.
     *
     * @return : encrypted message {String}
     * @parameter : encodedImages {list of encode chunk images}
     */

    public static String decodeMessage(List<Bitmap> encodedImages) {

        //Creating object
        MessageDecodingStatus messageDecodingStatus = new MessageDecodingStatus();

        for (Bitmap bit : encodedImages) {
            int[] pixels = new int[bit.getWidth() * bit.getHeight()];

            bit.getPixels(pixels, 0, bit.getWidth(), 0, 0, bit.getWidth(),
                    bit.getHeight());

            byte[] b;

            b = Utility.convertArray(pixels);

            decodeMessage(b, bit.getWidth(), bit.getHeight(), messageDecodingStatus);

            if (messageDecodingStatus.isEnded())
                break;
        }

        return messageDecodingStatus.getMessage();
    }
    private static class MessageDecodingStatus {

        private String message;
        private boolean ended;

        MessageDecodingStatus() {
            message = "";
            ended = false;
        }

        boolean isEnded() {
            return ended;
        }

        void setEnded() {
            this.ended = true;
        }

        String getMessage() {
            return message;
        }

        void setMessage(String message) {
            this.message = message;
        }


    }
}

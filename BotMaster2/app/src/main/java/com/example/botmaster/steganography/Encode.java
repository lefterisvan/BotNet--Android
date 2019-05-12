package com.example.botmaster.steganography;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.example.botmaster.Utility;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Encode
{
    private static final String TAG = Encode.class.getName();
    //start and end message constants
    private static final String END_MESSAGE_COSTANT = "#!@";
    private static final String START_MESSAGE_COSTANT = "@!#";
    private static final int[] binary = {16, 8, 0};
    private static final byte[] andByte = {(byte) 0xC0, 0x30, 0x0C, 0x03};
    private static final int[] toShift = {6, 4, 2, 0};

    private static byte[] encodeMessage(int[] integer_pixel_array, int image_columns, int image_rows, MessageEncodingStatus messageEncodingStatus, ProgressHandler progressHandler)
    {
        //denotes RGB channels
        int channels = 3;
        int shiftIndex = 4;
        //creating result byte_array
        byte[] result = new byte[image_rows * image_columns * channels];
        int resultIndex = 0;

        for (int row = 0; row < image_rows; row++)
        {
            for (int col = 0; col < image_columns; col++)
            {
                //2D matrix in 1D
                int element = row * image_columns + col;
                byte tmp;

                for (int channelIndex = 0; channelIndex < channels; channelIndex++)
                {
                    if (!messageEncodingStatus.isMessageEncoded())
                    {
                        // Shifting integer value by 2 in left and replacing the two least significant digits with the message_byte_array values..
                        tmp = (byte) ((((integer_pixel_array[element] >> binary[channelIndex]) & 0xFF) & 0xFC) | ((messageEncodingStatus.getByteArrayMessage()[messageEncodingStatus.getCurrentMessageIndex()] >> toShift[(shiftIndex++)
                                % toShift.length]) & 0x3));// 6

                        if (shiftIndex % toShift.length == 0)
                        {
                            messageEncodingStatus.incrementMessageIndex();

                            if (progressHandler != null)
                                progressHandler.increment(1);
                        }

                        if (messageEncodingStatus.getCurrentMessageIndex() == messageEncodingStatus.getByteArrayMessage().length)
                        {
                            messageEncodingStatus.setMessageEncoded();

                            if (progressHandler != null)
                                progressHandler.finished();
                        }
                    }
                    else
                    {
                        //Simply copy the integer to result array
                        tmp = (byte) ((((integer_pixel_array[element] >> binary[channelIndex]) & 0xFF)));
                    }
                    result[resultIndex++] = tmp;
                }
            }
        }
        return result;
    }


    public static List<Bitmap> encodeMessage(List<Bitmap> splitted_images, String encrypted_message, ProgressHandler progressHandler)
    {
        //Making result method
        List<Bitmap> result = new ArrayList<>(splitted_images.size());

        //Adding start and end message constants to the encrypted message
        encrypted_message = encrypted_message + END_MESSAGE_COSTANT;
        encrypted_message = START_MESSAGE_COSTANT + encrypted_message;

        //getting byte array from string
        byte[] byte_encrypted_message = encrypted_message.getBytes(Charset.forName("ISO-8859-1"));

        //Message Encoding Status
        MessageEncodingStatus message = new MessageEncodingStatus(byte_encrypted_message, encrypted_message);

        //Progress Handler
        if (progressHandler != null) {
            progressHandler.setTotal(encrypted_message.getBytes(Charset.forName("ISO-8859-1")).length);
        }

        //Just a log to get the byte message length
        Log.i(TAG, "Message length " + byte_encrypted_message.length);

        for (Bitmap bitmap : splitted_images)
        {
            if (!message.isMessageEncoded())
            {
                //getting bitmap height and width
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                //Making 1D integer pixel array
                int[] oneD = new int[width * height];
                bitmap.getPixels(oneD, 0, width, 0, 0, width, height);

                //getting bitmap density
                int density = bitmap.getDensity();

                //encoding image
                byte[] encodedImage = encodeMessage(oneD, width, height, message, progressHandler);

                //converting byte_image_array to integer_array
                int[] oneDMod = Utility.byteArrayToIntArray(encodedImage);

                //creating bitmap from encrypted_image_array
                Bitmap encoded_Bitmap = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888);
                encoded_Bitmap.setDensity(density);

                int masterIndex = 0;

                //setting pixel values of above bitmap
                for (int j = 0; j < height; j++)

                    for (int i = 0; i < width; i++)
                    {
                        encoded_Bitmap.setPixel(i, j, Color.argb(0xFF, oneDMod[masterIndex] >> 16 & 0xFF, oneDMod[masterIndex] >> 8 & 0xFF, oneDMod[masterIndex++] & 0xFF));
                    }

                result.add(encoded_Bitmap);
            }
            else
            {
                //Just add the image chunk to the result
                result.add(bitmap.copy(bitmap.getConfig(), false));
            }
        }
        return result;
    }

    //Progress handler class
    public interface ProgressHandler
    {
        void setTotal(int tot);
        void increment(int inc);
        void finished();
    }

    private static class MessageEncodingStatus
    {
        private boolean messageEncoded;
        private int currentMessageIndex;
        private byte[] byteArrayMessage;
        private String message;

        MessageEncodingStatus(byte[] byteArrayMessage, String message)
        {
            this.messageEncoded = false;
            this.currentMessageIndex = 0;
            this.byteArrayMessage = byteArrayMessage;
            this.message = message;
        }

        void incrementMessageIndex() {
            currentMessageIndex++;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        boolean isMessageEncoded() {
            return messageEncoded;
        }

        void setMessageEncoded() {
            this.messageEncoded = true;
        }

        int getCurrentMessageIndex() {
            return currentMessageIndex;
        }

        public void setCurrentMessageIndex(int currentMessageIndex)
        {
            this.currentMessageIndex = currentMessageIndex;
        }

        byte[] getByteArrayMessage() {
            return byteArrayMessage;
        }

        public void setByteArrayMessage(byte[] byteArrayMessage)
        {
            this.byteArrayMessage = byteArrayMessage;
        }
    }


}

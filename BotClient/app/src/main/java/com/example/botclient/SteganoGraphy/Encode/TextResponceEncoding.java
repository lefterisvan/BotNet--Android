package com.example.botclient.SteganoGraphy.Encode;

import com.example.botclient.SteganoGraphy.Images;

/*
*
* Interface that helps as return the decoded Image object back to the Bot class
* after the AsyncTask in TextDecoding terminates
*
 */
public interface TextResponceEncoding
{

    void ImageEncoder(Images output);

}

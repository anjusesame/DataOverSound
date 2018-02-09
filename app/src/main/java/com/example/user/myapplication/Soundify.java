package com.example.user.myapplication;

import android.app.Activity;

/**
 * This tool enable you to use the Soundify, with this <br>
 * you can transfer data to another near device through audio.<br>
 *
 * @author Raphael Fuchter - raphael.fuchter@gmail.com
 * @version 0.0.1
 */
public class Soundify extends BytesUtils {

    /**
     * This variable contains the current Activity of your app.
     */
    private Activity activity;

    /**
     * This Constant contains the instance of listener
     */
    public static SoundifyListener soundifyListener;

    /**
     * This function is used to initialize the Soundify instance
     */
    public Soundify(Activity activity) {
        this.activity = activity;
    }

    /**
     * This function makes the library starts to listen.
     *
     * @since 0.0.1
     */
//    public void startListening()
//    {
//        NewActivity.inicializeReceiver();
//    }

    /**
     * This function makes the library stop to listen.
     *
     * @since 0.0.1
     */
//    public void stopListening()  {
//        Receiver.getReceiver().stopReceiver();
//    }

    /**
     * This function transmit the message.
     *
     * @param data Send's data.
     * @since 0.0.1
     */
//    public void send(byte[] data) {
//        Sender.getSender().send(activity, data);
//
//    }

    /**
     * This implementation is required to treat the actions of the Soundify
     *
     * @since 0.0.1
     */
    public interface SoundifyListener {

        /**
         * This is called when the Soundify receive a data.
         *
         * @param data data received.
         * @since 0.0.1
         */
        void OnReceiveData(byte[] data);

        /**
         * This is called when the Soundify receive an error.
         * TODO Use this to return error!
         *
         * @param code Error code.
         * @param msg  Error message.
         * @since 0.0.1
         */
        //void OnReceiveError(int code, String msg);
    }

    /**
     * This function is used to set your listener of Soundify events.
     *
     * @param listener SoundifyListener instance.
     * @since 0.0.1
     */
    public void setSoundifyListener(SoundifyListener listener) {
        soundifyListener = listener;
    }

}

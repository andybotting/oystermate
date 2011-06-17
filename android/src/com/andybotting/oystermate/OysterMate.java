package com.andybotting.oystermate;

import android.content.Context;

public class OysterMate extends android.app.Application {

    private static OysterMate instance;

	/**
	 * Store the application context
	 */
    public OysterMate() {
        instance = this;
    }

    /**
     * This allows us to get the context anywhere within the application by importing
     * OysterMate, and calling OysterMate.getContext() 
     * @return Context
     */
    public static Context getContext() {
        return instance;
    }
	
	
}

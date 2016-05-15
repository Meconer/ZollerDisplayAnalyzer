/**
 * Copyright Mats Andersson, Mecona Teknik AB
 */
package se.mecona.zollerDisplayAnalyzer.gui;

import com.google.common.eventbus.EventBus;

/**
 * Singelton class for global data.
 * @author Mats
 */
public class Globals {
    private static EventBus eventBus = new EventBus();
    private void Globals() {
        
    }

    /**
     * 
     * @return the static eventbus instance
     */
    public static EventBus getEventBus() {
        return eventBus;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.gui;

import com.google.common.eventbus.EventBus;

/**
 *
 * @author Mats
 */
public class Globals {
    private static EventBus eventBus = new EventBus();
    private void Globals() {
        
    }

    public static EventBus getEventBus() {
        return eventBus;
    }
    
    
}

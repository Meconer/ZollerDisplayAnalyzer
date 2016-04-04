/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

/**
 *
 * @author Mats
 * @param <T>
 */
public class IOResult<T> {
    private final T data;
    private final boolean ok;
    
    public IOResult(boolean ok, T data) {
        this.ok = ok;
        this.data = data;
    }
    
    public boolean isOk() {
        return ok;
    }
    
    public boolean hasData() {
        return data != null;
    }
    
    public T getData() {
        return data;
    }
}

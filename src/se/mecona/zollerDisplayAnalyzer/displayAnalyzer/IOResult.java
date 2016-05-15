/*
 * Copyright Mats Andersson, Mecona Teknik AB
 */
package se.mecona.zollerDisplayAnalyzer.displayAnalyzer;

/**
 * Class for embedding data and status from an io operation like for example
 * reading a file.
 * @author Mats Andersson Mecona Teknik AB
 * @param <T>   The type of the data to get
 */
public class IOResult<T> {
    private final T data;
    private final boolean ok;
    
    /**
     * Constructor. Set ok to true if the io operation went ok. False otherwise.
     * If it is ok the data should be put in the data parameter.
     * @param ok    Boolean flag to tell that the result is ok.
     * @param data  The data that is the result of the io operation.
     */
    public IOResult(boolean ok, T data) {
        this.ok = ok;
        this.data = data;
    }
    
    /**
     * Checks to se if the io operation went ok
     * @return true if operation went ok
     */
    public boolean isOk() {
        return ok;
    }
    
    /**
     * 
     * @return true if data is available.
     */
    public boolean hasData() {
        return data != null;
    }
    
    /**
     *
     * @return the data that is the result from the io operation.
     */
    public T getData() {
        return data;
    }
}

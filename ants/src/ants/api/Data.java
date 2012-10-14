package ants.api;

public class Data {

    private Object dataObject;
    private String mimeType;
    
    public Data(Object dataObject, String mimeType) {
        this.dataObject = dataObject;
        this.mimeType = mimeType;
    }

    public Object getObject() {
        return this.dataObject;
    }

    public String getMimeType() {
        return this.mimeType;
    }
}

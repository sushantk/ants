package ants.api;

public class Data {

    private Object dataObject;
    private String mimeType;
    private String charSet = "";
    
    private String uri = "";
    
    public Data(Object dataObject, String mimeType, String charSet) {
        this.dataObject = dataObject;
        this.mimeType = mimeType;
        this.charSet = charSet;
    }

    public Object getObject() {
        return this.dataObject;
    }

    public String getMimeType() {
        return this.mimeType;
    }
    
    public String getCharSet() {
        return this.charSet;
    }
    
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

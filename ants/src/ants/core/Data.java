package ants.core;

public class Data {

    private Object data;
    private String mimeType;
    private String charSet;
    
    //TODO: aspect of this data as other pojos
    // private List<Data> aspects;
    
    private String uri = "";

    public Data(Object data, String mimeType) {
        this(data, mimeType, "");
    }

    public Data(Object data, String mimeType, String charSet) {
        this.data = data;
        this.mimeType = mimeType;
        this.charSet = charSet;
    }

    public Object getData() {
        return this.data;
    }
    
    public Data getAspect(Class<?> klass) {
        // object/klass.getname mimetype
        return null;
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

package ants;

public class Data {

    private byte[] bytes;
    private String mimeType;
    
    public Data(byte[] bytes, String mimeType) {
        this.bytes = bytes;
        this.mimeType = mimeType;
    }

    byte[] getBytes() {
        return this.bytes;
    }

    String getMimeType() {
        return this.mimeType;
    }
}

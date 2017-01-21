package com.fiberhome.vapp.inspector.encryption;


public class DefaultCodec implements Codec {

    public String decode(byte[] encrypted) throws Exception {
        if(encrypted == null){
            return null;
        }
        return new String(encrypted);
    }

    public byte[] encode(String data) throws Exception {
        if (data == null) {
            return new byte[0];
        }
        return data.getBytes();
    }

}

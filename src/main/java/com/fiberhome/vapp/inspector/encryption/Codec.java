package com.fiberhome.vapp.inspector.encryption;


public interface Codec {
    /**
     * @param data
     *            - the data to be encrypted
     * @return the encrypted data
     * @throws Exception
     */
    byte[] encode(String data) throws Exception;

    /**
     * @param encrypted
     *            - the data to be decrypted
     * @return the decrypted data
     * @throws Exception
     */
    String decode(byte[] encrypted) throws Exception;
}

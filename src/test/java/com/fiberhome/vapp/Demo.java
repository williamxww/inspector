package com.fiberhome.vapp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vv
 * @since 2017/3/28.
 */
public class Demo {

    public static void main(String[] args) throws Exception {

        while (true) {
            System.in.read();
            System.out.println("add data");
            List<Data> dataList = new ArrayList();
            for (int i = 0; i < 10000; i++) {
                Data data = new Data();
                dataList.add(data);
            }
        }

    }
}

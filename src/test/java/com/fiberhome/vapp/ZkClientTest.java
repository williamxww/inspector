package com.fiberhome.vapp;

import com.fiberhome.vapp.inspector.common.ZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vv
 * @since 2017/1/21.
 */
public class ZkClientTest {

    @Test
    public void connect() throws IOException, KeeperException, InterruptedException, NoSuchAlgorithmException {
        List<ACL> acls = new ArrayList<ACL>();
        // 添加第一个id，采用用户名密码形式
        Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest("admin:admin"));
        ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
        acls.add(acl1);
        // 添加第二个id，所有用户可读权限
        Id id2 = new Id("world", "anyone");
        ACL acl2 = new ACL(ZooDefs.Perms.READ, id2);
        acls.add(acl2);
        ZkClient client = new ZkClient("127.0.0.1:2181", 20000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getPath() + " " + event.getType());
            }
        });

//        client.create("/test/aa", "vv".getBytes(), acls, CreateMode.PERSISTENT);
        print(client.getData("/test/aa", true, null));
        client.addAuthInfo("digest", "admin:admin".getBytes());
        client.setData("/test/aa", "hello".getBytes(), 0);
        print(client.getData("/test/aa", true, null));
    }

    public void print(byte[] data){
        System.out.println(new String(data));
    }
}

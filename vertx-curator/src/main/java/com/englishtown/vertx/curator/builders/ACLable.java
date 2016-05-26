package com.englishtown.vertx.curator.builders;

import org.apache.zookeeper.data.ACL;

import java.util.List;

/**
 * An operation builder with ACLs
 */
public interface ACLable<T> {

    T withACL(List<ACL> aclList);

}

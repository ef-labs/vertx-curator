package com.englishtown.vertx.curator;

import org.apache.curator.framework.api.CuratorEvent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * An executable curator operation
 */
public interface CuratorOperation {

    /**
     * Execute the operation with the provided client and callback handler
     *
     * @param client  curator client
     * @param handler callback handler
     * @throws Exception
     */
    void execute(CuratorClient client, Handler<AsyncResult<CuratorEvent>> handler) throws Exception;

}

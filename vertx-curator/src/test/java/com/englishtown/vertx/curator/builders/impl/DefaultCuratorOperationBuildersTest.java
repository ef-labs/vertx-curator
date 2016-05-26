package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.builders.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Provider;

import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultCuratorOperationBuildersTest {

    @Mock
    Provider<CreateBuilder> createBuilderProvider;

    @Mock
    Provider<GetDataBuilder> getDataBuilderProvider;

    @Mock
    Provider<SetDataBuilder> setDataBuilderProvider;

    @Mock
    Provider<GetACLBuilder> getACLBuilderProvider;

    @Mock
    Provider<SetACLBuilder> setACLBuilderProvider;

    @Mock
    Provider<GetChildrenBuilder> getChildrenBuilderProvider;

    @Mock
    Provider<ExistsBuilder> existsBuilderProvider;

    @Mock
    Provider<DeleteBuilder> deleteBuilderProvider;

    @Test
    public void testDefaultCuratorOperationBuilders() throws Exception {
        DefaultCuratorOperationBuilders target = new DefaultCuratorOperationBuilders(
                createBuilderProvider
                , getDataBuilderProvider
                , setDataBuilderProvider
                , getACLBuilderProvider
                , setACLBuilderProvider
                , getChildrenBuilderProvider
                , existsBuilderProvider
                , deleteBuilderProvider
        );

        target.checkExists();
        verify(existsBuilderProvider).get();

        target.create();
        verify(createBuilderProvider).get();

        target.delete();
        verify(deleteBuilderProvider).get();

        target.getACL();
        verify(getACLBuilderProvider).get();

        target.setACL();
        verify(setACLBuilderProvider).get();

        target.getChildren();
        verify(getChildrenBuilderProvider).get();

        target.getData();
        verify(getDataBuilderProvider).get();

        target.setData();
        verify(setDataBuilderProvider).get();
    }
}

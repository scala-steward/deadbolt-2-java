/*
 * Copyright 2010-2016 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.objectify.deadbolt.java.test.security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.ConstraintPoint;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.models.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Example handler just to show how to handle DI of multiple implementations of DeadboltHandler.
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
@HandlerQualifiers.SomeOtherHandler
public class SomeOtherDeadboltHandler extends AbstractDeadboltHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SomeOtherDeadboltHandler.class);

    @Override
    public CompletionStage<Optional<? extends Subject>> getSubject(final Http.RequestHeader requestHeader)
    {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<Result>> beforeAuthCheck(final Http.RequestHeader requestHeader, final Optional<String> content)
    {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.RequestHeader requestHeader)
    {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public void onAuthSuccess(Http.RequestHeader requestHeader,
                              String constraintType,
                              ConstraintPoint constraintPoint)
    {
        LOGGER.info("[{} - {}] - authorization succeeded for [{}]",
                    constraintPoint,
                    constraintType,
                    requestHeader.attrs());
    }
}

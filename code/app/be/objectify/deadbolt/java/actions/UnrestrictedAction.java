/*
 * Copyright 2012 Steve Chaloner
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
package be.objectify.deadbolt.java.actions;

import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.JavaAnalyzer;
import be.objectify.deadbolt.java.cache.HandlerCache;
import be.objectify.deadbolt.java.cache.SubjectCache;
import play.Configuration;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Implements the {@link Unrestricted} functionality, i.e. there are no restrictions on the resource.
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
public class UnrestrictedAction extends AbstractDeadboltAction<Unrestricted>
{
    @Inject
    public UnrestrictedAction(final JavaAnalyzer analyzer,
                              final SubjectCache subjectCache,
                              final HandlerCache handlerCache,
                              final Configuration config,
                              final ExecutionContextProvider ecProvider)
    {
        super(analyzer,
              subjectCache,
              handlerCache,
              config,
              ecProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public F.Promise<Result> execute(final Http.Context ctx) throws Throwable
    {
        final ExecutionContext executionContext = executionContextProvider.get();
        final F.Promise<Result> promise = F.Promise.promise(() -> isActionUnauthorised(ctx),
                                                            executionContext)
                                                   .flatMap(unauthorised -> {
                                                       final F.Promise<Result> result;
                                                       if (unauthorised)
                                                       {
                                                           result = onAuthFailure(getDeadboltHandler(configuration.handlerKey()),
                                                                                  configuration.content(),
                                                                                  ctx);
                                                       }
                                                       else
                                                       {
                                                           markActionAsAuthorised(ctx);
                                                           result = delegate.call(ctx);
                                                       }
                                                       return result;
                                                   }, executionContext);

        return blocking ? F.Promise.pure(promise.get(blockingTimeout,
                                                     TimeUnit.MILLISECONDS))
                        : promise;
    }
}

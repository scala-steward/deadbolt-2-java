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

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

/**
 * A dynamic restriction is user-defined, and so completely arbitrary.  Hence, no checks on subjects, etc, occur
 * here.
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
public class DynamicAction extends AbstractRestrictiveAction<Dynamic>
{
    public DynamicAction()
    {
        // no-op
    }

    public DynamicAction(final Dynamic configuration,
                         final Action<?> delegate)
    {
        this.configuration = configuration;
        this.delegate = delegate;
    }

    @Override
    public F.Promise<Result> applyRestriction(final Http.Context ctx,
                                              final DeadboltHandler deadboltHandler) throws Throwable
    {
        final DynamicResourceHandler resourceHandler = deadboltHandler.getDynamicResourceHandler(ctx);

        if (resourceHandler == null)
        {
            throw new RuntimeException("A dynamic resource is specified but no dynamic resource handler is provided");
        }
        else
        {
            return F.Promise.promise(new F.Function0<Boolean>()
            {
                @Override
                public Boolean apply() throws Throwable
                {
                    return resourceHandler.isAllowed(getValue(),
                                                     getMeta(),
                                                     deadboltHandler,
                                                     ctx);
                }
            }).flatMap(new F.Function<Boolean, F.Promise<Result>>()
            {
                @Override
                public F.Promise<Result> apply(final Boolean allowed) throws Throwable
                {
                    final F.Promise<Result> result;
                    if (allowed)
                    {
                        markActionAsAuthorised(ctx);
                        result = delegate.call(ctx);
                    }
                    else
                    {
                        markActionAsUnauthorised(ctx);
                        result = onAuthFailure(deadboltHandler,
                                               configuration.content(),
                                               ctx);
                    }
                    return result;
                }
            });
        }
    }

    public String getMeta()
    {
        return configuration.meta();
    }

    public String getValue()
    {
        return configuration.value();
    }

    @Override
    public String getHandlerKey()
    {
        return configuration.handlerKey();
    }

    @Override
    public Class<? extends DeadboltHandler> getDeadboltHandlerClass()
    {
        return configuration.handler();
    }
}

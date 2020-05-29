/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.impl;

import static org.jooq.SQLDialect.FIREBIRD;
import static org.jooq.impl.Keywords.K_CASCADE;
import static org.jooq.impl.Keywords.K_DOMAIN;
import static org.jooq.impl.Keywords.K_DROP;
import static org.jooq.impl.Keywords.K_IF_EXISTS;
import static org.jooq.impl.Keywords.K_RESTRICT;
import static org.jooq.impl.Keywords.K_TYPE;

import java.util.Set;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Domain;
import org.jooq.DropDomainCascadeStep;
import org.jooq.DropDomainFinalStep;
import org.jooq.SQLDialect;

/**
 * The <code>DROP DOMAIN IF EXISTS</code> statement.
 */
@SuppressWarnings({ "rawtypes", "unused" })
final class DropDomainImpl
extends
    AbstractRowCountQuery
implements
    DropDomainCascadeStep,
    DropDomainFinalStep
{

    private static final long serialVersionUID = 1L;

    private final Domain<?> domain;
    private final boolean   dropDomainIfExists;
    private       Boolean   cascade;

    DropDomainImpl(
        Configuration configuration,
        Domain domain,
        boolean dropDomainIfExists
    ) {
        this(
            configuration,
            domain,
            dropDomainIfExists,
            null
        );
    }

    DropDomainImpl(
        Configuration configuration,
        Domain domain,
        boolean dropDomainIfExists,
        Boolean cascade
    ) {
        super(configuration);

        this.domain = domain;
        this.dropDomainIfExists = dropDomainIfExists;
        this.cascade = cascade;
    }

    final Domain<?> $domain()             { return domain; }
    final boolean   $dropDomainIfExists() { return dropDomainIfExists; }
    final Boolean   $cascade()            { return cascade; }

    // -------------------------------------------------------------------------
    // XXX: DSL API
    // -------------------------------------------------------------------------

    @Override
    public final DropDomainImpl cascade() {
        this.cascade = true;
        return this;
    }

    @Override
    public final DropDomainImpl restrict() {
        this.cascade = false;
        return this;
    }

    // -------------------------------------------------------------------------
    // XXX: QueryPart API
    // -------------------------------------------------------------------------



    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedBy(FIREBIRD);

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.family());
    }

    @Override
    public final void accept(Context<?> ctx) {
        if (dropDomainIfExists && !supportsIfExists(ctx)) {
            Tools.beginTryCatch(ctx, DDLStatementType.DROP_DOMAIN);
            accept0(ctx);
            Tools.endTryCatch(ctx, DDLStatementType.DROP_DOMAIN);
        }
        else
            accept0(ctx);
    }

    private final void accept0(Context<?> ctx) {
        switch (ctx.family()) {






            default:
                ctx.visit(K_DROP).sql(' ').visit(K_DOMAIN);
                break;
        }

        if (dropDomainIfExists && supportsIfExists(ctx))
            ctx.sql(' ').visit(K_IF_EXISTS);

        ctx.sql(' ').visit(domain);

        if (cascade != null)
            if (cascade)
                ctx.sql(' ').visit(K_CASCADE);
            else
                ctx.sql(' ').visit(K_RESTRICT);
    }


}

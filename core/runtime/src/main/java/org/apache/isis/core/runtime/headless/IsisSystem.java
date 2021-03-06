/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.runtime.headless;

import static org.apache.isis.commons.internal.base._Casts.uncheckedCast;

import java.util.Set;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelInvalidException;
import org.apache.isis.core.runtime.authentication.AuthenticationManager;
import org.apache.isis.core.runtime.authentication.AuthenticationRequest;
import org.apache.isis.core.runtime.headless.auth.AuthenticationRequestNameOnly;
import org.apache.isis.core.runtime.logging.IsisLoggingConfigurer;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;
import org.apache.isis.core.runtime.system.session.IsisSessionFactoryBuilder;
import org.apache.isis.core.runtime.systemusinginstallers.IsisComponentProvider;

import com.google.common.base.Joiner;


/**
 * Wraps a plain {@link IsisSessionFactoryBuilder}.
 *
 * <p>
 *     This is a simplification of <tt>IsisSystemForTest</tt>, removing dependencies on junit and specsupport.
 * </p>
 */
public class IsisSystem {

    // -- getElseNull, get, set

    protected static ThreadLocal<IsisSystem> ISFT = new ThreadLocal<>();

    public static IsisSystem getElseNull() {
        return ISFT.get();
    }

    public static IsisSystem get() {
        final IsisSystem isft = ISFT.get();
        if(isft == null) {
            throw new IllegalStateException("No IsisSystem available on thread; call #set(IsisSystem) first");
        }

        return isft;
    }

    public static void set(IsisSystem isft) {
        ISFT.set(isft);
    }

    // -- Builder

    public static class Builder<T extends Builder<T, S>, S extends IsisSystem> {

        protected AuthenticationRequest authenticationRequest = new AuthenticationRequestNameOnly("tester");

        protected IsisConfigurationDefault configuration = new IsisConfigurationDefault();

        protected AppManifest appManifestIfAny;

        protected org.apache.log4j.Level level;

        public T with(IsisConfiguration configuration) {
            this.configuration = (IsisConfigurationDefault) configuration;
            return uncheckedCast(this);
        }

        public T with(AuthenticationRequest authenticationRequest) {
            this.authenticationRequest = authenticationRequest;
            return uncheckedCast(this);
        }

        public T with(AppManifest appManifest) {
            this.appManifestIfAny = appManifest;
            return uncheckedCast(this);
        }

        public T withLoggingAt(org.apache.log4j.Level level) {
            this.level = level;
            return uncheckedCast(this);
        }

        public S build() {
            final IsisSystem isisSystem =
                    new IsisSystem(
                            appManifestIfAny,
                            configuration,
                            authenticationRequest);
            return configure(uncheckedCast(isisSystem));
        }

        protected S configure(final S isisSystem) {
            if(level != null) {
                isisSystem.setLevel(level);
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public synchronized void run() {
                    try {
                        isisSystem.closeSession();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if(isisSystem.isisSessionFactory != null) {
                            isisSystem.isisSessionFactory.destroyServicesAndShutdown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return isisSystem;
        }

    }

    public static <T extends Builder<T, S>, S extends IsisSystem> Builder<T, S> builder() {
        return new Builder<>();
    }

    // -- constructor, fields

    // these fields 'xxxForComponentProvider' are used to initialize the IsisComponentProvider, but shouldn't be used thereafter.
    protected final AppManifest appManifestIfAny;
    protected final IsisConfiguration configurationOverride;

    protected final AuthenticationRequest authenticationRequestIfAny;
    protected AuthenticationSession authenticationSession;


    protected IsisSystem(
            final AppManifest appManifestIfAny,
            final IsisConfiguration configurationOverride,
            final AuthenticationRequest authenticationRequestIfAny) {
        this.appManifestIfAny = appManifestIfAny;
        this.configurationOverride = configurationOverride;
        this.authenticationRequestIfAny = authenticationRequestIfAny;
    }

    

    // -- level
    private org.apache.log4j.Level level = org.apache.log4j.Level.INFO;

    /**
     * The level to use for the root logger if fallback (ie a <tt>logging.properties</tt> file cannot be found).
     */
    public org.apache.log4j.Level getLevel() {
        return level;
    }

    public void setLevel(org.apache.log4j.Level level) {
        this.level = level;
    }

    

    // -- setup (also componentProvider)

    // populated at #setupSystem
    protected IsisComponentProvider componentProvider;

    IsisSystem setUpSystem() throws RuntimeException {
        try {
            initIfRequiredThenOpenSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    protected void initIfRequiredThenOpenSession() throws Exception {

        // exit as quickly as possible for this case...
        final MetaModelInvalidException mmie = IsisContext.getMetaModelInvalidExceptionIfAny();
        if(mmie != null) {
            final Set<String> validationErrors = mmie.getValidationErrors();
            final String validationMsg = Joiner.on("\n").join(validationErrors);
            throw new AssertionError(validationMsg);
        }

        boolean firstTime = isisSessionFactory == null;
        if(firstTime) {
            IsisLoggingConfigurer isisLoggingConfigurer = new IsisLoggingConfigurer(getLevel());
            isisLoggingConfigurer.configureLogging(".", new String[] {});

            componentProvider = new IsisComponentProviderDefault(
                    appManifestIfAny,
                    configurationOverride
            );

            final IsisSessionFactoryBuilder isisSessionFactoryBuilder = new IsisSessionFactoryBuilder(componentProvider, DeploymentCategory.PRODUCTION, appManifestIfAny);

            // ensures that a FixtureClock is installed as the singleton underpinning the ClockService
            FixtureClock.initialize();

            isisSessionFactory = isisSessionFactoryBuilder.buildSessionFactory();
            // REVIEW: does no harm, but is this required?
            closeSession();

            // if the IsisSystem does not initialize properly, then - as a side effect - the resulting
            // MetaModelInvalidException will be pushed onto the IsisContext (as a static field).
            final MetaModelInvalidException ex = IsisContext.getMetaModelInvalidExceptionIfAny();
            if (ex != null) {

                // for subsequent tests; the attempt to bootstrap the framework will leave
                // the IsisContext singleton as set.
                IsisContext.clear();

                final Set<String> validationErrors = ex.getValidationErrors();
                final StringBuilder buf = new StringBuilder();
                for (String validationError : validationErrors) {
                    buf.append(validationError).append("\n");
                }
                throw new AssertionError("Metamodel is invalid: \n" + buf.toString());
            }
        }

        final AuthenticationManager authenticationManager = isisSessionFactory.getAuthenticationManager();
        authenticationSession = authenticationManager.authenticate(authenticationRequestIfAny);

        openSession();
    }

    // -- isisSystem (populated during setup)
    protected IsisSessionFactory isisSessionFactory;

    /**
     * The {@link IsisSessionFactory} created during {@link #setUpSystem()}.
     */
    public IsisSessionFactory getIsisSessionFactory() {
        return isisSessionFactory;
    }

    /**
     * The {@link AuthenticationSession} created during {@link #setUpSystem()}.
     */
    public AuthenticationSession getAuthenticationSession() {
        return authenticationSession;
    }

    // -- openSession, closeSession, nextSession


    public void nextSession() throws Exception {
        closeSession();
        openSession();
    }

    public void openSession() throws Exception {
        openSession(authenticationSession);
    }

    public void openSession(AuthenticationSession authenticationSession) throws Exception {
        isisSessionFactory.openSession(authenticationSession);
    }

    public void closeSession() throws Exception {
        if(isisSessionFactory!=null && isisSessionFactory.inSession()) {
            isisSessionFactory.closeSession();
        }
    }

    // -- getService

    public <C> C getService(Class<C> serviceClass) {
        final ServicesInjector servicesInjector = isisSessionFactory.getServicesInjector();
        return servicesInjector.lookupServiceElseFail(serviceClass);
    }

}

package com.makotan.ninja.mirage.test;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.makotan.ninja.mirage.NinjaMirageModule;
import jp.sf.amateras.mirage.session.Session;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import org.junit.After;
import org.junit.Before;

/**
 * Created by makotan on 2014/03/09.
 */
public abstract class NinjaMirageTestBase {
    /**
     * Guice Injector to get DAOs
     */
    private Injector injector;

    private NinjaMode ninjaMode;

    Session session;

    /**
     * Constructor checks if NinjaMode was set in System properties, if not,
     * NinjaMode.test is used as default
     */
    public NinjaMirageTestBase() {
        Optional<NinjaMode> mode = NinjaModeHelper
                .determineModeFromSystemProperties();
        ninjaMode = mode.isPresent() ? mode.get() : NinjaMode.test;
    }

    /**
     * Constructor, receives the test mode to choose the database
     *
     * @param testMode
     */
    public NinjaMirageTestBase(NinjaMode testMode) {
        ninjaMode = testMode;
    }

    @Before
    public final void initialize() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
        NinjaMirageModule nmm = new NinjaMirageModule(ninjaProperties);
        injector = Guice.createInjector(nmm);
        session = nmm.getSession();
        session.begin();
    }

    @After
    public final void stop() {
        session.rollback();
    }

    /**
     * Get the DAO instances ready to use
     *
     * @param clazz
     * @return DAO
     */
    protected <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}

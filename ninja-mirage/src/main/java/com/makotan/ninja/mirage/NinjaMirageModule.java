package com.makotan.ninja.mirage;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import jp.sf.amateras.mirage.SqlManager;
import jp.sf.amateras.mirage.integration.guice.TransactionInterceptor;
import jp.sf.amateras.mirage.integration.guice.Transactional;
import jp.sf.amateras.mirage.session.Session;
import jp.sf.amateras.mirage.session.SessionFactory;
import ninja.utils.NinjaProperties;

import java.util.Properties;

/**
 * original {@link jp.sf.amateras.mirage.session.SessionFactory}
 * User: kuroeda.makoto
 * Date: 14/02/27
 * Time: 15:56
 */
public class NinjaMirageModule extends AbstractModule {

    Provider<NinjaProperties> ninjaPropertiesProvider;
    NinjaProperties ninjaProperties;
    volatile Properties properties = null;

    public NinjaMirageModule(Provider<NinjaProperties> ninjaPropertiesProvider) {
        this.ninjaPropertiesProvider = ninjaPropertiesProvider;
    }

    public NinjaMirageModule(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    protected void configure() {
        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(Transactional.class),
                new TransactionInterceptor());
    }

    NinjaProperties getNinjaProperties() {
        if (ninjaPropertiesProvider == null) {
            return ninjaProperties;
        }
        return ninjaPropertiesProvider.get();
    }

    @Provides
    @Singleton
    public Session getSession(){
        if (properties == null) {
            synchronized (this) {
                if (properties == null) {
                    NinjaProperties ninjaProperties = getNinjaProperties();
                    Properties properties = new Properties();
                    copyProperties(properties, ninjaProperties, "jdbc.driver");
                    copyProperties(properties, ninjaProperties, "jdbc.url" , "db.connection.url");
                    copyProperties(properties, ninjaProperties, "jdbc.user" , "db.connection.username");
                    copyProperties(properties, ninjaProperties, "jdbc.password" , "db.connection.password");
                    copyProperties(properties, ninjaProperties, "sql.cache");
                    copyProperties(properties, ninjaProperties, "dbcp.max_active");
                    copyProperties(properties, ninjaProperties, "dbcp.min_idle");
                    copyProperties(properties, ninjaProperties, "dbcp.max_wait");
                    this.properties = properties;
                }
            }
        }
        return SessionFactory.getSession(properties);
    }
    
    private void copyProperties(Properties properties , NinjaProperties ninjaProperties , String key) {
        String s = ninjaProperties.get(key);
        if (s != null) {
            properties.put(key , s);
        }
    }

    private void copyProperties(Properties properties , NinjaProperties ninjaProperties , String key , String subKey) {
        String s = ninjaProperties.get(key);
        if (s != null) {
            properties.put(key , s);
        }
        s = ninjaProperties.get(subKey);
        if (s != null) {
            properties.put(key , s);
        }
    }

    @Provides
    @Singleton
    public SqlManager getSqlManager(Session session){
        return session.getSqlManager();
    }}

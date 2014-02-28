ninja-mirage
============

ninja-mirage module


setup
============

git clone の後に mvn install

mavenのpom.xml にこれを追加

    <dependency>
        <groupId>com.makotan.ninja</groupId>
        <artifactId>ninja-mirage</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>


ninja conf
============

application.confにmirageの定義を追加


    jdbc.driver=driver name (option)
    jdbc.url=jdbc:mysql://host/db
    jdbc.user=username
    jdbc.password=password

%testや%devもOK

conf/Module.javaにNinjaMirageModuleを追加

    Provider<NinjaProperties> ninjaPropertiesProvider = getProvider(NinjaProperties.class);
    install(new NinjaMirageModule(ninjaPropertiesProvider));


必須ではないけど、コンストラクタはこれにするとログがSlf4jで出力されるようになる

    public Module() {
        super();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }


mirage
============

versionは 1.x.x で対応(1.2.0はNG)

詳しくは[Mirage](http://amateras.sourceforge.jp/site/mirage/index.html "Mirage")



Transactionの利用

    import jp.sf.amateras.mirage.integration.guice.Transactional;

injectionは

    @Inject
    SqlManager sqlManager;


methodの前に `@Transactional` を設定する

あとはmirageにしたがって

    List<RetClass> resultList = sqlManager.
        getResultList(RetClass.class, new ClasspathSqlResource("/META-INF/2way.sql"), param);

package learningtest;

import kr.co.mytour.learningtest.user.sqlservice.EmbeddedDbSqlRegistry;
import kr.co.mytour.learningtest.user.sqlservice.OxmSqlService;
import kr.co.mytour.learningtest.user.sqlservice.SqlRegistry;
import kr.co.mytour.learningtest.user.sqlservice.SqlService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@Configuration
public class SqlServiceContext {

    @Bean
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();

        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());

        return sqlService;
    }

//    @Resource   // Autowired와 다르게 필드 이름으로 가져온다; 기존 dataSource와 충돌 방지용...
//    DataSource embeddedDataBase;

    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();

        //sqlRegistry.setDataSource(this.embeddedDataBase);
        sqlRegistry.setDataSource(embeddedDataBase());

        return sqlRegistry;
    }

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath("kr.co.mytour.learningtest.user.sqlservice.jaxb");

        return marshaller;
    }

    @Bean
    public DataSource embeddedDataBase() {
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:learningtest/embeddeddb/schema.sql")
                .build();
    }
}

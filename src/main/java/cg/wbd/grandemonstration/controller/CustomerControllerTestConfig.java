package cg.wbd.grandemonstration.controller;

import cg.wbd.grandemonstration.service.CustomerService;
import cg.wbd.grandemonstration.service.ProvinceService;
import cg.wbd.grandemonstration.service.impl.CustomerServiceImplWithSpringData;
import cg.wbd.grandemonstration.service.impl.ProvinceServiceImplWithSpringData;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@ComponentScan("cg.wbd.grandemonstration")
public class CustomerControllerTestConfig {
    @Bean
    public CustomerService customerService() {
        return Mockito.mock(CustomerServiceImplWithSpringData.class);
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("cms")
                .build();
    }

    @Bean
    public ProvinceService provinceService() {
        return Mockito.mock(ProvinceServiceImplWithSpringData.class);
    }

    @Autowired
    private ProvinceService provinceService;

}
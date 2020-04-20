package com.larbotech.batch.job;

import com.larbotech.batch.config.AbstractTests;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;

@Configuration
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
    scripts = "classpath:db/sql/test-dao-donnees-agregat-jour-create-table.sql",
    config = @SqlConfig(dataSource = "dataSourcePspe", transactionManager = "transactionManagerPspe"))
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
    scripts = "classpath:org/springframework/batch/core/schema-drop-hsqldb.sql",
    config = @SqlConfig(dataSource = "dataSource", transactionManager = "transactionManager"))
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
    scripts = "classpath:org/springframework/batch/core/schema-hsqldb.sql",
    config = @SqlConfig(dataSource = "dataSource", transactionManager = "transactionManager"))
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
    scripts = "classpath:db/sql/test-dao-canal-create-db.sql",
    config = @SqlConfig(dataSource = "dataSourcePspe", transactionManager = "transactionManagerPspe"))
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
    scripts = "classpath:db/sql/test-dao-canal-insert-data.sql",
    config = @SqlConfig(dataSource = "dataSourcePspe", transactionManager = "transactionManagerPspe"))

public class AbstractJobTests extends AbstractTests {

}

package config;


import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import liquibase.*;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.ui.LoggerUIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;


@Slf4j
@Component
public class LiquibaseConf {
    private final HikariDataSource dataSource;

    public LiquibaseConf(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void liquibaseUpdateTool() {
        log.info("------------------> liquibaseUpdateTool Init <------------------");
        try {
            Connection connection = dataSource.getConnection();
            log.info("----> Connected to: {}.{} <----",
                    dataSource.getConnection().getCatalog(),
                    dataSource.getConnection().getSchema());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new liquibase.Liquibase("/changelog/changelog-master.xml", new ClassLoaderResourceAccessor(), database);
            Scope.enter(Map.of(Scope.Attr.ui.name(), new NullUIService()));
            liquibase.setShowSummary(UpdateSummaryEnum.OFF);
            liquibase.setShowSummaryOutput(UpdateSummaryOutputEnum.LOG);
            Contexts contexts = new Contexts();
            LabelExpression labelExpression = new LabelExpression();
            Date baseDate = new Date();
            try {
                liquibase.update(contexts, labelExpression);
                log.info("----> Migrazioni eseguite con successo <----");
            } catch (Exception e) {
                liquibase.rollback(baseDate, null, contexts, labelExpression);
                log.error("----> Si è verificato un errore in fase di applicazione delle migrazioni. E' Stato eseguito il rollback <----");
                log.error(ExceptionUtils.getStackTrace(e));
            }
        } catch (Exception e) {
            log.error("----> Si è verificato un errore in fase di migrazione DB <----");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        log.info("------------------> liquibaseUpdateTool End <------------------");

    }

    private static class NullUIService extends LoggerUIService {
        @Override
        public void sendMessage(String message) {
        }

        @Override
        public void sendErrorMessage(String message, Throwable exception) {
        }
    }
}

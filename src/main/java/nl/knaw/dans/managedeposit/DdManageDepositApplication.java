/*
 * Copyright (C) 2023 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.knaw.dans.managedeposit;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.dans.managedeposit.core.CsvMessageBodyWriter;
import nl.knaw.dans.managedeposit.core.DepositProperties;
import nl.knaw.dans.managedeposit.core.service.InboxWatcherFactoryImpl;
import nl.knaw.dans.managedeposit.core.watcherservice.Watcher;
import nl.knaw.dans.managedeposit.db.DepositPropertiesDAO;
import nl.knaw.dans.managedeposit.health.InboxHealthCheck;
import nl.knaw.dans.managedeposit.resources.DepositPropertiesResource;

public class DdManageDepositApplication extends Application<DdManageDepositConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DdManageDepositApplication().run(args);
    }

    private final HibernateBundle<DdManageDepositConfiguration> hibernateBundle =
        new HibernateBundle<DdManageDepositConfiguration>(DepositProperties.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(DdManageDepositConfiguration configuration) {
                return  configuration.getDepositPropertiesDatabase();
            }
        };

    @Override
    public String getName() {
        return "Dd Manage Deposit";
    }

    @Override
    public void initialize(final Bootstrap<DdManageDepositConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final DdManageDepositConfiguration configuration, final Environment environment) {
        DepositPropertiesDAO depositPropertiesDAO = new DepositPropertiesDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new DepositPropertiesResource(depositPropertiesDAO, hibernateBundle.getSessionFactory()));

        environment.healthChecks().register("Inbox", new InboxHealthCheck(configuration));

        //final var inboxWatcherFactory = new InboxWatcherFactoryImpl();
//        final var collectTaskManager = new CollectTaskManager(configuration.getCollect().getInboxes(), configuration.getExtractMetadata().getInbox(), configuration.getCollect().getPollingInterval(),
//            collectExecutorService, transferItemService, metadataReader, fileService, inboxWatcherFactory);

        environment.jersey().register(new CsvMessageBodyWriter());

        final var watcher = new Watcher("data/auto-ingest");
        watcher.startWatcher();
    }

}

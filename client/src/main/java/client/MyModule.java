package client;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import java.util.Locale;
import java.util.ResourceBundle;

import client.scenes.*;
import client.utils.*;

public class MyModule implements Module {

    /**
     * Configures module.
     *
     * @param binder Binder instance.
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(StartScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EventOverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(InviteScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddParticipantCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddExpenseCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddTagCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EditTagCtrl.class).in(Scopes.SINGLETON);
        binder.bind(DebtOverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SettingsScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(LoginScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AdminPanelCtrl.class).in(Scopes.SINGLETON);
        binder.bind(StatisticsScreenCtrl.class).in(Scopes.SINGLETON);

        binder.bind(EmailManager.class).in(Scopes.SINGLETON);
        binder.bind(ExchangeManager.class).in(Scopes.SINGLETON);
        binder.bind(ServerUtils.class).in(Scopes.SINGLETON);

        ConfigManager configManager = new ConfigManager("config.properties");
        binder.bind(ConfigManager.class).toInstance(configManager);
        binder.bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("languages.labels", new Locale(configManager.getLanguage())));
    }
}

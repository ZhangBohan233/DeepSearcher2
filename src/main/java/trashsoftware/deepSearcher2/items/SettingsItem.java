package trashsoftware.deepSearcher2.items;

import trashsoftware.deepSearcher2.controllers.settingsPages.SettingsPage;

public class SettingsItem {

    private String name;
    private SettingsPage page;

    public SettingsItem(String name, SettingsPage page) {
        this.name = name;
        this.page = page;
    }

    public SettingsPage getPage() {
        return page;
    }

    @Override
    public String toString() {
        return name;
    }
}

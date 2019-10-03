package trashsoftware.deepSearcher2.items;

import trashsoftware.deepSearcher2.controllers.settingsPages.Page;
import trashsoftware.deepSearcher2.controllers.settingsPages.SettingsPage;

public class SettingsItem {

    private String name;
    private Page page;

    public SettingsItem(String name, Page page) {
        this.name = name;
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public String toString() {
        return name;
    }
}

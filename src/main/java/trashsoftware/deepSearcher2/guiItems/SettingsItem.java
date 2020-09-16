package trashsoftware.deepSearcher2.guiItems;

import trashsoftware.deepSearcher2.controllers.settingsPages.Page;

public class SettingsItem {

    private final String name;
    private final Page page;

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

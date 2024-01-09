package client.event.listeners;

import client.event.Event;
import client.setting.Setting;

public class EventSettingClicked extends Event<EventSettingClicked> {

	Setting setting;

    public EventSettingClicked(Setting setting) {
        this.setting = setting;
    }

    public Setting getMessage() {
        return setting;
    }

    public void setMessage(Setting setting) {
        this.setting = setting;
    }
}

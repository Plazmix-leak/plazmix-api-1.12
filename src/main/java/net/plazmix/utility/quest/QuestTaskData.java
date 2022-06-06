package net.plazmix.utility.quest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.plazmix.utility.JsonUtil;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestTaskData {

    final int taskId;
    int progress;

    final long timeGetMillis;

    public String toJson() {
        return JsonUtil.toJson(this);
    }
}

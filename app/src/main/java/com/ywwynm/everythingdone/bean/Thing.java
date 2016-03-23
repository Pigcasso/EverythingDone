package com.ywwynm.everythingdone.bean;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.ywwynm.everythingdone.Definitions;
import com.ywwynm.everythingdone.R;
import com.ywwynm.everythingdone.helpers.CheckListHelper;
import com.ywwynm.everythingdone.utils.DisplayUtil;

import static com.ywwynm.everythingdone.Definitions.LimitForGettingThings.ALL_DELETED;
import static com.ywwynm.everythingdone.Definitions.LimitForGettingThings.ALL_FINISHED;
import static com.ywwynm.everythingdone.Definitions.LimitForGettingThings.ALL_UNDERWAY;
import static com.ywwynm.everythingdone.Definitions.LimitForGettingThings.GOAL_UNDERWAY;
import static com.ywwynm.everythingdone.Definitions.LimitForGettingThings.HABIT_UNDERWAY;
import static com.ywwynm.everythingdone.Definitions.LimitForGettingThings.NOTE_UNDERWAY;
import static com.ywwynm.everythingdone.Definitions.LimitForGettingThings.REMINDER_UNDERWAY;

/**
 * Created by ywwynm on 2015/5/21.
 * Model layer. Related to table things.
 */
public class Thing implements Parcelable {

    public static final int HEADER                = -1;
    public static final int NOTE                  = 0;
    public static final int REMINDER              = 1;
    public static final int HABIT                 = 2;
    public static final int GOAL                  = 3;
    public static final int WELCOME_UNDERWAY      = 4;
    public static final int WELCOME_NOTE          = 5;
    public static final int WELCOME_REMINDER      = 6;
    public static final int WELCOME_HABIT         = 7;
    public static final int WELCOME_GOAL          = 8;
    public static final int NOTIFICATION_UNDERWAY = 9;
    public static final int NOTIFICATION_NOTE     = 10;
    public static final int NOTIFICATION_REMINDER = 11;
    public static final int NOTIFICATION_HABIT    = 12;
    public static final int NOTIFICATION_GOAL     = 13;

    public static final int NOTIFY_EMPTY_UNDERWAY = 14;
    public static final int NOTIFY_EMPTY_NOTE     = 15;
    public static final int NOTIFY_EMPTY_REMINDER = 16;
    public static final int NOTIFY_EMPTY_HABIT    = 17;
    public static final int NOTIFY_EMPTY_GOAL     = 18;
    public static final int NOTIFY_EMPTY_FINISHED = 19;
    public static final int NOTIFY_EMPTY_DELETED  = 20;

    public static final int UNDERWAY              = 0;
    public static final int FINISHED              = 1;
    public static final int DELETED               = 2;
    public static final int DELETED_FOREVER       = 3;

    public static final Parcelable.Creator<Thing> CREATOR = new Parcelable.Creator<Thing>() {

        @Override
        public Thing createFromParcel(Parcel source) {
            return new Thing(source);
        }

        @Override
        public Thing[] newArray(int size) {
            return new Thing[size];
        }
    };

    private long   id;
    private int    type;
    private int    state;
    private int    color;
    private String title;
    private String content;
    private String attachment;
    private long   location;
    private long   createTime;
    private long   updateTime;
    private long   finishTime;

    private boolean selected;

    public Thing(long id, int type, int color, long location) {
        this(id, type, UNDERWAY, color, "", "", "", location,
                System.currentTimeMillis(), System.currentTimeMillis(), 0);
    }

    public Thing(long id, int type, int state, int color, String title, String content, String attachment,
                 long location, long createTime, long updateTime, long finishTime) {
        this.id         = id;
        this.type       = type;
        this.state      = state;
        this.color      = color;
        this.title      = title;
        this.content    = content;
        this.attachment = attachment;
        this.location   = location;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.finishTime = finishTime;

        selected = false;
    }

    public Thing(Thing thing) {
        id         = thing.id;
        type       = thing.type;
        state      = thing.state;
        color      = thing.color;
        title      = thing.title;
        content    = thing.content;
        attachment = thing.attachment;
        location   = thing.location;
        createTime = thing.createTime;
        updateTime = thing.updateTime;
        finishTime = thing.finishTime;
        selected   = thing.selected;
    }

    public Thing(Parcel in) {
        id         = in.readLong();
        type       = in.readInt();
        state      = in.readInt();
        color      = in.readInt();
        title      = in.readString();
        content    = in.readString();
        attachment = in.readString();
        location   = in.readLong();
        createTime = in.readLong();
        updateTime = in.readLong();
        finishTime = in.readLong();
    }

    public Thing(Cursor c) {
        this(c.getLong(0),
             c.getInt(1),
             c.getInt(2),
             c.getInt(3),
             c.getString(4),
             c.getString(5),
             c.getString(6),
             c.getLong(7),
             c.getLong(8),
             c.getLong(9),
             c.getLong(10));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setLocation(long location) {
        this.location = location;
    }

    public long getLocation() {
        return location;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static String getTypeStr(int type, Context context) {
        if (type == NOTE) {
            return context.getString(R.string.note);
        } else if (type == REMINDER) {
            return context.getString(R.string.reminder);
        } else if (type == HABIT) {
            return context.getString(R.string.habit);
        } else if (type == GOAL) {
            return context.getString(R.string.goal);
        } else return context.getString(R.string.thing);
    }

    public static int[] getLimits(int type, int state) {
        int[] limits;
        if (state == FINISHED || type == NOTIFY_EMPTY_FINISHED) {
            limits = new int[] { Definitions.LimitForGettingThings.ALL_FINISHED };
        } else if (state == DELETED || type == NOTIFY_EMPTY_DELETED) {
            limits = new int[] { Definitions.LimitForGettingThings.ALL_DELETED };
        } else {
            if (type == WELCOME_UNDERWAY || type == NOTIFICATION_UNDERWAY
                    || type == NOTIFY_EMPTY_UNDERWAY) {
                return new int[] { Definitions.LimitForGettingThings.ALL_UNDERWAY };
            } else if (type == WELCOME_NOTE || type == NOTIFICATION_NOTE
                    || type == NOTIFY_EMPTY_NOTE) {
                return new int[] { Definitions.LimitForGettingThings.NOTE_UNDERWAY };
            } else if (type == WELCOME_REMINDER || type == NOTIFICATION_REMINDER
                    || type == NOTIFY_EMPTY_REMINDER) {
                return new int[] { Definitions.LimitForGettingThings.REMINDER_UNDERWAY };
            } else if (type == WELCOME_HABIT || type == NOTIFICATION_HABIT
                    || type == NOTIFY_EMPTY_HABIT) {
                return new int[] { Definitions.LimitForGettingThings.HABIT_UNDERWAY };
            } else if (type == WELCOME_GOAL || type == NOTIFICATION_GOAL
                    || type == NOTIFY_EMPTY_GOAL) {
                return new int[] { Definitions.LimitForGettingThings.GOAL_UNDERWAY };
            } else {
                limits = new int[2];
                limits[0] = Definitions.LimitForGettingThings.ALL_UNDERWAY;
                if (type == REMINDER) {
                    limits[1] = Definitions.LimitForGettingThings.REMINDER_UNDERWAY;
                } else if (type == HABIT) {
                    limits[1] = Definitions.LimitForGettingThings.HABIT_UNDERWAY;
                } else if (type == GOAL) {
                    limits[1] = Definitions.LimitForGettingThings.GOAL_UNDERWAY;
                } else {
                    limits[1] = Definitions.LimitForGettingThings.NOTE_UNDERWAY;
                }
            }
        }
        return limits;
    }

    public static boolean isTypeStateMatchLimit(int type, int state, int limit) {
        if (state == Thing.DELETED_FOREVER) {
            return false;
        }
        int[] limits = getLimits(type, state);
        for (int lim : limits) {
            if (limit == lim) {
                return true;
            }
        }
        return false;
    }

    public static int getNotifyEmptyType(int limit) {
        switch (limit) {
            case ALL_UNDERWAY:
                return Thing.NOTIFY_EMPTY_UNDERWAY;
            case NOTE_UNDERWAY:
                return Thing.NOTIFY_EMPTY_NOTE;
            case REMINDER_UNDERWAY:
                return Thing.NOTIFY_EMPTY_REMINDER;
            case HABIT_UNDERWAY:
                return Thing.NOTIFY_EMPTY_HABIT;
            case GOAL_UNDERWAY:
                return Thing.NOTIFY_EMPTY_GOAL;
            case ALL_FINISHED:
                return Thing.NOTIFY_EMPTY_FINISHED;
            case ALL_DELETED:
                return Thing.NOTIFY_EMPTY_DELETED;
            default:
                return Thing.NOTIFY_EMPTY_UNDERWAY;
        }
    }

    public static Thing generateNotifyEmpty(int limit, long headerId, Context context) {
        Thing thing = new Thing(headerId, getNotifyEmptyType(limit),
                DisplayUtil.getRandomColor(context), headerId);
        switch (limit) {
            case ALL_UNDERWAY:
                thing.setTitle(context.getString(R.string.congratulations));
                thing.setContent(context.getString(R.string.empty_underway));
                break;
            case NOTE_UNDERWAY:
                thing.setContent(context.getString(R.string.empty_note));
                break;
            case REMINDER_UNDERWAY:
                thing.setContent(context.getString(R.string.empty_reminder));
                break;
            case HABIT_UNDERWAY:
                thing.setTitle(context.getString(R.string.congratulations));
                thing.setContent(context.getString(R.string.empty_habit));
                break;
            case GOAL_UNDERWAY:
                thing.setContent(context.getString(R.string.empty_goal));
                break;
            case ALL_FINISHED:
                thing.setContent(context.getString(R.string.empty_finished));
                break;
            case ALL_DELETED:
                thing.setContent(context.getString(R.string.empty_deleted));
                break;
        }
        return thing;
    }

    public static Thing getSameCheckStateThing(Thing thing, int stateBefore, int stateAfter) {
        Thing result = thing;
        if (stateBefore == UNDERWAY && stateAfter == FINISHED) {
            String content = thing.getContent();
            if (content.contains(CheckListHelper.SIGNAL + 0)) {
                result = new Thing(thing);
                result.setContent(content.replaceAll(
                        CheckListHelper.SIGNAL + 0, CheckListHelper.SIGNAL + 1));
            }
        } else if (stateBefore == FINISHED && stateAfter == UNDERWAY) {
            String content = thing.getContent();
            if (content.contains(CheckListHelper.SIGNAL + 1)) {
                result = new Thing(thing);
                result.setContent(content.replaceAll(
                        CheckListHelper.SIGNAL + 1, CheckListHelper.SIGNAL + 0));
            }
        }
        return result;
    }

    public static boolean noUpdate(Thing thing, String title, String content, String attachment,
                                   int type, int color) {
        return thing.title.equals(title)
            && thing.content.equals(content)
            && thing.attachment.equals(attachment)
            && thing.type == type
            && thing.color == color;
    }

    public static boolean isImportantType(int type) {
        return type == HABIT || type == GOAL;
    }

    public static boolean isReminderType(int type) {
        return type == REMINDER || type == GOAL;
    }

    public static boolean sameType(int type1, int type2) {
        if (type1 == type2) return true;
        if (type1 == WELCOME_UNDERWAY) return true;
        if (type1 == WELCOME_REMINDER && type2 == REMINDER) {
            return true;
        }
        if (type1 == WELCOME_HABIT && type2 == HABIT) {
            return true;
        }
        if (type1 == WELCOME_GOAL && type2 == GOAL) {
            return true;
        }
        return false;
    }

    public boolean matchSearchRequirement(String keyword, int color) {
        if (this.color != color && color != -1979711488 && color != 0) {
            return false;
        }

        String curContent = content;
        if (CheckListHelper.isSignalContainsStrIgnoreCase(keyword)) {
            String regex = "";
            for (int i = 0; i < CheckListHelper.CHECK_STATE_NUM; i++) {
                regex += CheckListHelper.SIGNAL + i + "|";
            }
            regex = regex.substring(0, regex.length() - 1);
            curContent = curContent.replaceAll(regex, "");
        }
        return curContent.contains(keyword);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && id == ((Thing) o).id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(type);
        dest.writeInt(state);
        dest.writeInt(color);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(attachment);
        dest.writeLong(location);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeLong(finishTime);
    }
}
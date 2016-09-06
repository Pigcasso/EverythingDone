package com.ywwynm.everythingdone.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.ywwynm.everythingdone.App;
import com.ywwynm.everythingdone.Def;
import com.ywwynm.everythingdone.R;
import com.ywwynm.everythingdone.appwidgets.AppWidgetHelper;
import com.ywwynm.everythingdone.database.HabitDAO;
import com.ywwynm.everythingdone.database.ReminderDAO;
import com.ywwynm.everythingdone.database.ThingDAO;
import com.ywwynm.everythingdone.managers.ThingManager;
import com.ywwynm.everythingdone.model.Habit;
import com.ywwynm.everythingdone.model.Reminder;
import com.ywwynm.everythingdone.model.Thing;

import java.util.List;

/**
 * Created by ywwynm on 2016/9/4.
 * Helper class for execute actions that happen not in ThingsActivity or DetailActivity.
 * For example, action finish in a notification for Reminder will finally call method here.
 */
public class RemoteActionHelper {

    public static void finishReminder(Context context, Thing thing, int position) {
        if (position == -1) {
            thing = Thing.getSameCheckStateThing(thing, Thing.UNDERWAY, Thing.FINISHED);
            ThingDAO thingDAO = ThingDAO.getInstance(context);
            long hId = thingDAO.getHeaderId();
            thingDAO.updateState(thing, thing.getLocation(), Thing.UNDERWAY, Thing.FINISHED,
                    true,  /* handleNotifyEmpty  */
                    true,  /* handleCurrentLimit */
                    false, /* toUndo             */
                    hId,
                    true   /* shouldUpdateHeader */);
        }
        updateUiEverywhere(context, thing, position, thing.getType(),
                Def.Communication.RESULT_UPDATE_THING_STATE_DIFFERENT);
    }

    public static void finishHabitOnce(Context context, Thing thing, int position, long time) {
        HabitDAO habitDAO = HabitDAO.getInstance(context);
        Habit habit = habitDAO.getHabitById(thing.getId());
        int typeBefore = thing.getType();
        if (habit == null) {
            correctIfNoHabit(context, thing, position, typeBefore);
            return;
        }

        if (habit.allowFinish(time)) {
            habitDAO.finishOneTime(habit);
            updateUiEverywhere(context, thing, position, typeBefore,
                    Def.Communication.RESULT_UPDATE_THING_DONE_TYPE_SAME);
        } else {
            if (habit.getRecord().isEmpty() && habit.getRemindedTimes() == 0) {
                Toast.makeText(context, R.string.sb_cannot_finish_habit_first_time,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, R.string.sb_cannot_finish_habit_more_times,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void delay10Minutes(Context context, Thing thing, int position) {
        ReminderDAO dao = ReminderDAO.getInstance(context);
        Reminder reminder = dao.getReminderById(thing.getId());
        int typeBefore = thing.getType();
        if (reminder == null) {
            correctIfNoReminder(context, thing, position, typeBefore);
            return;
        }

        if (position == -1) {
            ThingDAO.getInstance(context).update(typeBefore, thing, false, false);
        } else {
            ThingManager.getInstance(context).update(typeBefore, thing, position, false);
        }
        reminder.setNotifyTime(System.currentTimeMillis() + 10 * 60 * 1000);
        reminder.setNotifyMillis(System.currentTimeMillis() - reminder.getNotifyTime()
                + reminder.getNotifyMillis() + 10 * 60 * 1000);
        reminder.setState(Reminder.UNDERWAY);
        reminder.setUpdateTime(System.currentTimeMillis());
        dao.update(reminder);

        updateUiEverywhere(context, thing, position, typeBefore,
                Def.Communication.RESULT_UPDATE_THING_DONE_TYPE_SAME);
    }

    public static void toggleChecklistItem(Context context, long id, int itemPos) {
        Pair<Thing, Integer> pair = App.getThingAndPosition(context, id, -1);
        Thing thing = pair.first;
        if (thing == null) {
            return;
        }
        String updatedContent = getUpdatedContent(thing.getContent(), itemPos);
        thing.setContent(updatedContent);
        int position = pair.second;
        int typeBefore = thing.getType();
        if (position == -1) {
            ThingDAO.getInstance(context).update(typeBefore, thing, true, true);
        }
        updateUiEverywhere(context, thing, position, typeBefore,
                Def.Communication.RESULT_UPDATE_THING_DONE_TYPE_SAME);
    }

    private static String getUpdatedContent(String content, int itemPos) {
        List<String> items = CheckListHelper.toCheckListItems(content, false);
        items.remove("2");
        items.remove("3");
        items.remove("4");
        String oldItem = items.get(itemPos);
        items.remove(itemPos);
        if (oldItem.startsWith("0")) { // unfinished to finished
            String newItem = "1" + oldItem.substring(1, oldItem.length());
            int firstFinishedIndex = CheckListHelper.getFirstFinishedItemIndex(items);
            if (firstFinishedIndex == -1) {
                items.add(newItem);
            } else {
                items.add(firstFinishedIndex, newItem);
            }
        } else {
            String newItem = "0" + oldItem.substring(1, oldItem.length());
            items.add(0, newItem);
        }
        return CheckListHelper.toCheckListStr(items);
    }

    public static void correctIfNoReminder(
            Context context, Thing thing, int position, int typeBefore) {
        if (Thing.isReminderType(typeBefore)) {
            thing.setType(Thing.NOTE);
            if (position == -1) {
                ThingDAO.getInstance(context).update(typeBefore, thing, true, true);
            }
            RemoteActionHelper.updateUiEverywhere(
                    context, thing, position, typeBefore,
                    Def.Communication.RESULT_UPDATE_THING_DONE_TYPE_DIFFERENT);
        }
    }

    public static void correctIfNoHabit(
            Context context, Thing thing, int position, int typeBefore) {
        if (typeBefore == Thing.HABIT) {
            thing.setType(Thing.NOTE);
            if (position == -1) {
                ThingDAO.getInstance(context).update(typeBefore, thing, true, true);
            }
            updateUiEverywhere(context, thing, position, typeBefore,
                    Def.Communication.RESULT_UPDATE_THING_DONE_TYPE_DIFFERENT);
        }
    }

    /**
     * Update UI for ThingsActivity and app widgets if a remote action happened.
     * This method will also finish the action if it acts with a thing that under current limit,
     * which means it can be found in {@link ThingManager#mThings}. In this situation, we should
     * call methods in {@link ThingManager}, get their returned values and put them into broadcast
     * {@link Intent}s, as a result of which, ThingsActivity can handle UI update correctly as well
     * as appropriately.
     *
     * @param context the context where the action happened.
     * @param thing the thing that the action act with.
     * @param position position of {@param thing} inside {@link ThingManager#mThings}. This can
     *                 be -1 if {@param thing} couldn't be found under current limit.
     * @param typeBefore used when we are updating {@param thing}'s type.
     * @param resultCode although this method can handle all possible resultCodes declared in
     *                   {@link com.ywwynm.everythingdone.Def.Communication}, remote actions will
     *                   only produce following resultCodes for the time being:
     *                   1. RESULT_UPDATE_THING_STATE_DIFFERENT: for finishing a Reminder/Goal.
     *                   2. RESULT_UPDATE_THING_DONE_TYPE_SAME: for finishing a Habit once, or delay
     *                      a Reminder.
     *                   3. RESULT_UPDATE_THING_DONE_TYPE_DIFFERENT: for finding a wrong thing or
     *                      not finding a correct related Reminder/Habit instance. For an example,
     *                      you can reference
     *                      {@link RemoteActionHelper#correctIfNoReminder(Context, Thing, int, int)}.
     */
    public static void updateUiEverywhere(
            Context context, Thing thing, int position, int typeBefore, int resultCode) {
        if (App.isSomethingUpdatedSpecially()) {
            App.setShouldJustNotifyDataSetChanged(true);
        } else {
            App.setSomethingUpdatedSpecially(true);
        }

        Intent broadcastIntent = new Intent(
                Def.Communication.BROADCAST_ACTION_UPDATE_MAIN_UI);
        broadcastIntent.putExtra(Def.Communication.KEY_RESULT_CODE, resultCode);
        broadcastIntent.putExtra(Def.Communication.KEY_THING, thing);
        broadcastIntent.putExtra(Def.Communication.KEY_POSITION, position);
        broadcastIntent.putExtra(Def.Communication.KEY_TYPE_BEFORE, typeBefore);

        ThingManager thingManager = ThingManager.getInstance(context);
        if (resultCode == Def.Communication.RESULT_UPDATE_THING_STATE_DIFFERENT) {
            broadcastIntent.putExtra(Def.Communication.KEY_STATE_AFTER, Thing.FINISHED);
            if (position != -1) {
                broadcastIntent.putExtra(Def.Communication.KEY_CALL_CHANGE,
                        thingManager.updateState(thing, position, thing.getLocation(), Thing.UNDERWAY,
                                Thing.FINISHED, false, true));
            }
        } else if (resultCode == Def.Communication.RESULT_UPDATE_THING_DONE_TYPE_DIFFERENT) {
            if (position != -1) {
                broadcastIntent.putExtra(Def.Communication.KEY_CALL_CHANGE,
                        thingManager.update(typeBefore, thing, position, true) == 1);
            }
        }
        context.sendBroadcast(broadcastIntent);

        AppWidgetHelper.updateSingleThingAppWidgets(context, thing.getId());
        AppWidgetHelper.updateThingsListAppWidgetsForType(context, typeBefore);
        int typeAfter = thing.getType();
        if (typeBefore != typeAfter) {
            AppWidgetHelper.updateThingsListAppWidgetsForType(context, typeAfter);
        }
    }

}
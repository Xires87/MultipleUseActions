package net.fryc.frycmua.item.action.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fryc.frycmua.MultipleUseActions;
import net.fryc.frycmua.util.UseActionHelper;
import net.fryc.frycmua.util.client.FileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PriorityManager {

    public static final PriorityManager DEFAULT_PRIORITY_MANAGER = FileHelper.getMainPriorityManager();

    private final ArrayList<PriorityManager.Entry> entries;
    private final HashSet<Identifier> unknownEntries;
    private ActionPriority actionPriority;


    public PriorityManager(ActionPriority actionPriority) {
        this(actionPriority, new ArrayList<>());
    }

    public PriorityManager(ActionPriority actionPriority, ArrayList<PriorityManager.Entry> entries) {
        this.actionPriority = actionPriority;
        this.unknownEntries = new HashSet<>();
        this.entries = new ArrayList<>(this.actionPriority.addOtherUseActions(entries));
    }


    public void moveDown(PriorityManager.Entry entry) {
        if(this.canMoveDown(entry)){
            this.move(entry, 1);
        }
    }

    public boolean canMoveDown(PriorityManager.Entry entry) {
        if(!this.entries.contains(entry)) return false;

        return this.entries.indexOf(entry) < this.entries.size() - 1;
    }

    public void moveUp(PriorityManager.Entry entry) {
        if(this.canMoveUp(entry)){
            this.move(entry, -1);
        }
    }

    public boolean canMoveUp(PriorityManager.Entry entry) {
        if(!this.entries.contains(entry)) return false;

        return this.entries.indexOf(entry) > 0;
    }

    public void move(PriorityManager.Entry entry, int distance) {
        int i = this.entries.indexOf(entry);
        this.entries.remove(i);
        this.entries.add(i + distance, entry);
    }

    public void validate() {
        this.unknownEntries.clear();
        this.entries.removeIf(entry -> {
            if(entry.getActionId() == null) {
                MultipleUseActions.LOGGER.error("Found use action without identifier. Removing...");

                return true;
            }

            return false;
        });
        this.entries.stream().filter(entry -> !UseActionHelper.useActionExists(entry.getActionId())).forEach(entry -> {
            this.unknownEntries.add(entry.getActionId());
        });
    }

    public void addOtherUseActions() {
        List<PriorityManager.Entry> newList = this.actionPriority.addOtherUseActions(this.entries);
        this.entries.clear();
        this.entries.addAll(newList);
        this.validate();
    }

    public void deleteInvalidActions() {
        this.entries.removeIf(entry -> this.unknownEntries.contains(entry.getActionId()));
        this.unknownEntries.clear();
    }

    public List<Identifier> getOrderedAvailableUseActions(ItemStack mainHandStack, ItemStack offHandStack) {
        return this.getActionPriority().getOrderedUseActions(mainHandStack, offHandStack, this.getEntries().stream().filter(this::isEntryValid).toList());
    }

    public List<PriorityManager.Entry> getEntries() {
        return List.copyOf(this.entries);
    }

    public List<Identifier> getUnknownEntries() {
        return List.copyOf(this.unknownEntries);
    }

    public ActionPriority getActionPriority() {
        return this.actionPriority;
    }

    public void setActionPriority(ActionPriority priority){
        this.actionPriority = priority;
    }

    public boolean isEntryValid(PriorityManager.Entry entry) {
        return !this.unknownEntries.contains(entry.getActionId());
    }

    @Nullable
    public PriorityManager.Entry getEntryById(Identifier id) {
        return this.entries.stream().filter(entry -> entry.getActionId().equals(id)).findFirst().orElse(null);
    }


    @Environment(EnvType.CLIENT)
    public static class Entry {

        private boolean active;
        private boolean locked;
        private final Identifier actionId;

        public Entry(Identifier actionId, boolean active, boolean locked) {
            this.actionId = actionId;
            this.active = active;
            this.locked = locked;
        }

        public Entry(Identifier actionId, boolean active) {
            this(actionId, active, false);
        }

        public Entry(Identifier actionId) {
            this(actionId, false, false);
        }

        public Identifier getActionId() {
            return this.actionId;
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isLocked() {
            return this.locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }
}

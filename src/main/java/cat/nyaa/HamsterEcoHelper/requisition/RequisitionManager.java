package cat.nyaa.HamsterEcoHelper.requisition;

import cat.nyaa.HamsterEcoHelper.HamsterEcoHelper;
import cat.nyaa.HamsterEcoHelper.I18n;
import cat.nyaa.HamsterEcoHelper.Utils;
import cat.nyaa.HamsterEcoHelper.data.RequisitionSpecification;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RequisitionManager extends BukkitRunnable {
    private final HamsterEcoHelper plugin;
    private RequisitionInstance currentReq = null;
    public RequisitionManager(HamsterEcoHelper plugin) {
        this.plugin = plugin;
        int interval = plugin.config.requisitionIntervalTicks;
        runTaskTimer(plugin, interval, interval);
    }

    @Override
    public void run() {
        newRequisition();
    }

    public boolean newRequisition() {
        if (currentReq != null) return false;
        if (plugin.config.itemsForReq.isEmpty()) return false;
        RequisitionSpecification item = Utils.randomWithWeight(
                plugin.config.itemsForReq,
                (RequisitionSpecification i) -> i.randomWeight
        );
        if (item == null) return false;

        int unitPrice = Utils.inclusiveRandomInt(item.minPurchasePrice, item.maxPurchasePrice);
        int amount = item.maxAmount < 0? -1: Utils.inclusiveRandomInt(item.minAmount, item.maxAmount);
        currentReq = new RequisitionInstance(item, unitPrice, amount, plugin, ()->this.currentReq=null);
        return true;
    }

    public void halt() {
        if (currentReq != null)
            currentReq.halt();
        currentReq = null;
        Bukkit.broadcast(I18n.get("user.req.halted"), "heh.bid");
    }

    public RequisitionInstance getCurrentRequisition() {
        return currentReq;
    }
}
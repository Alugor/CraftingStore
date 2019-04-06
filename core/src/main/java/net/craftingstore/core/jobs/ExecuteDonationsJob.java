package net.craftingstore.core.jobs;

import net.craftingstore.core.CraftingStore;
import net.craftingstore.core.exceptions.CraftingStoreApiException;
import net.craftingstore.core.models.donation.Donation;

import java.util.AbstractMap;
import java.util.Arrays;

public class ExecuteDonationsJob {
    private CraftingStore instance;
    private Donation[] donations;

    public ExecuteDonationsJob(CraftingStore instance, Donation[] donations) throws CraftingStoreApiException {
        this.instance = instance;
        this.donations = donations;
        this.execute();
    }

    private void execute() throws CraftingStoreApiException {
        this.instance.getLogger().debug("Executing ExecuteDonationsJob.");
        int[] completedIds = Arrays.stream(this.donations)
                .map(donation -> new AbstractMap.SimpleEntry<>(donation, instance.getImplementation().executeDonation(donation)))
                .filter(AbstractMap.SimpleEntry::getValue)
                .mapToInt(entry -> entry.getKey().getId())
                .toArray();
        if (completedIds.length > 0) {
            this.instance.getLogger().debug("Marking executed donations as complete.");
            instance.getApi().completeDonations(completedIds);
        }
        this.instance.getLogger().debug("Execution done.");
    }
}
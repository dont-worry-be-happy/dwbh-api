package dwbh.api.fetchers;

import dwbh.api.domain.Group;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Aggregates all fetchers responsible for handling
 * GraphQL requests
 *
 * @since 0.1.0
 */
@Singleton
public class FetcherProvider {

    private GroupFetcher groupFetcher;

    /**
     * Returns an instance of {@link GroupFetcher}
     *
     * @return an instance of {@link GroupFetcher}
     * @since 0.1.0
     */
    public GroupFetcher getGroupFetcher() {
        return groupFetcher;
    }

    /**
     * Sets the instance responsible to handle request
     * over {@link Group}
     *
     * @param groupFetcher instance of {@link GroupFetcher}
     * @since 0.1.0
     */
    @Inject
    public void setGroupFetcher(GroupFetcher groupFetcher) {
        this.groupFetcher = groupFetcher;
    }
}

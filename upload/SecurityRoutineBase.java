import java.util.List;

/**
 * A SecurityRoutine class contains a collection of areas that need to be
 * checked by a security team.
 */
public abstract class SecurityRoutineBase {

    /**
     * Creates a new SecurityRoutineBase instance.
     */
    public SecurityRoutineBase() {}

    /**
     * Represents an area to be checked in the security routine.
     */
    static class AreaBase {

        private final String id; // area ID

        /**
         * Creates a new AreaBase instance with the given ID.
         * @param id area ID
         */
        public AreaBase(String id) {
            this.id = id;
        }

        /**
         * Returns the area's ID.
         * @return area ID
         */
        public String getId() {
            return id;
        }
    }

    /**
     * Adds the given area to the SecurityBase, and returns the added area.
     *
     * @param area area to add
     * @return area that was added
     */
    public abstract AreaBase insertArea(AreaBase area);

    /**
     * Adds the order between two areas, where area1 should be checked
     * before area2.
     *
     * @param area1 area that should be checked before
     * @param area2 area that should be checked after
     */
    public abstract void addOrder(AreaBase area1, AreaBase area2);

    /**
     * Calculates and returns the order in which the areas must be checked
     * by the security team.
     *
     * @return list of areas in order, or null if it is not possible to define
     * the order
     */
    public abstract List<AreaBase> calculateTotalOrder();
}

package jexplorer.fileexplorerclasses;

public enum SortOrders {

    TO_UP("TO_UP", 1), TO_DOWN("TO_DOWN", -1);

    private String name;
    private int order;

    SortOrders(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

}

import java.util.*;
import java.util.stream.Collectors;

public class SecurityRoutine extends SecurityRoutineBase {

    List<Node> nodeList = new ArrayList<>();
    /* Implement all the necessary methods here */

    /*
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        REMOVE THE MAIN FUNCTION BEFORE SUBMITTING TO THE AUTOGRADER
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        The following main function is provided for simple debugging only

        Note: to enable assertions, you need to add the "-ea" flag to the
        VM options of SecurityRoutine's run configuration
     */
   

    @Override
    public AreaBase insertArea(AreaBase area) {
        Node n = new Node(area);
        nodeList.add(n);
        return area;
    }

    @Override
    public void addOrder(AreaBase area1, AreaBase area2) {
        Node one = null;
        Node two = null;
        for (int i = 0; i < this.nodeList.size(); i++) {
            if (this.nodeList.get(i).areaBase.getId().equals(area1.getId())) {
                one = this.nodeList.get(i);
            }
            if (this.nodeList.get(i).areaBase.getId().equals(area2.getId())) {
                two = this.nodeList.get(i);
            }
        }
        one.after.add(two);
        one.order = one.order + 1;
        two.order = two.order + one.order +1;

    }

    @Override
    public List<AreaBase> calculateTotalOrder() {

        for (int j = 0; j < (this.nodeList.size()-1); j++) {
            Node first = this.nodeList.get(j);
            List<Node> firstAfter = this.nodeList.get(j).after;
            List<Node> total1 = this.nodeList.get(j).total;

            for (Node n : firstAfter) {
                SecurityRoutine.addTotal(total1, n);
            }

            for (int k = (j+1); k < this.nodeList.size(); k++) {
                Node two = this.nodeList.get(k);
                List<Node> twoAfter = this.nodeList.get(k).after;
                List<Node> total2 = this.nodeList.get(j).total;
                for (Node m : twoAfter) {
                    SecurityRoutine.addTotal(total2, m);
                }
                if (total2.contains(first) && total1.contains(two)) {
                    return null;
                }
            }
        }

        PriorityQueue<Node> queue = new PriorityQueue<>(new Node(0));
        for (int i = 0; i < this.nodeList.size(); i++) {
            if (this.nodeList.get(i).order != 0) {
                queue.add(this.nodeList.get(i));
            }
        }
        List<AreaBase> areaList = new ArrayList<>();


        while (!queue.isEmpty()) {
            Node temp = queue.poll();
            areaList.add(temp.areaBase);
        }
        return areaList;




    }

    public static void addTotal(List<Node> total, Node node) {
        for (int i = 0; i< node.after.size(); i++){
            if (!total.contains(node.after.get(i))) {
                total.add(node.after.get(i));
                SecurityRoutine.addTotal(total, node.after.get(i));
            }

        }
    }
}

class Node implements Comparator<Node>{
    SecurityRoutineBase.AreaBase areaBase;
    int order = 0;
    List<Node> after = new ArrayList<>();
    List<Node> total = new ArrayList<>();
    public Node(int i) {}

    public Node(SecurityRoutineBase.AreaBase a) {
        this.areaBase = a;
    }

    @Override
    public int compare(Node o1, Node o2) {
        if (o1.order > o2.order) {
            return 1;
        }
        return -1;
    }
}
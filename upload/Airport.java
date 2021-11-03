import java.util.*;
import java.util.stream.Collectors;

public class Airport extends AirportBase {

    private List<TerminalBase> terminals;
    private List<ShuttleBase> shuttles;

    public Airport(int capacity){
        super(capacity);
        this.terminals = new ArrayList<TerminalBase>();
        this.shuttles = new ArrayList<ShuttleBase>();
    }

    @Override
    public TerminalBase opposite(ShuttleBase shuttle, TerminalBase terminal) {
        if(shuttle.getOrigin().getId().equals(terminal.getId())) {
            return shuttle.getDestination();
        } else if (shuttle.getDestination().getId().equals(terminal.getId())) {
            return shuttle.getOrigin();
        }
        return null;
    }

    @Override
    public TerminalBase insertTerminal(TerminalBase terminal) {
        this.terminals.add(terminal);
        return terminal;
    }

    @Override
    public ShuttleBase insertShuttle(TerminalBase origin, TerminalBase destination, int time) {
        Shuttle shuttle = new Shuttle(origin, destination,time);
        shuttle.capacity = this.getCapacity();
        this.shuttles.add(shuttle);
        return shuttle;
    }

    @Override
    public boolean removeTerminal(TerminalBase terminal) {

        for (int i = 0; i < this.terminals.size(); i++) {
            if (this.terminals.get(i).getId().equals(terminal.getId())) {
                for (int j = 0; j < this.shuttles.size(); j++) {
                    if (this.shuttles.get(j).getOrigin().getId().equals(terminal.getId()) || this.shuttles.get(j).getDestination().getId().equals(terminal.getId())) {
                        boolean a = this.removeShuttle(this.shuttles.get(j));
                    }
                }
                this.terminals.remove(this.terminals.get(i));
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean removeShuttle(ShuttleBase shuttle) {
        for (int i = 0; i < this.shuttles.size(); i++) {
            if ((this.shuttles.get(i).getTime() == shuttle.getTime()) && (this.shuttles.get(i).getDestination().equals(shuttle.getDestination())) && (this.shuttles.get(i).getOrigin().equals(shuttle.getOrigin()))) {
                this.shuttles.remove(this.shuttles.get(i));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ShuttleBase> outgoingShuttles(TerminalBase terminal) {
        List<ShuttleBase> temp = new ArrayList<ShuttleBase>();
        for (int i = 0; i < this.shuttles.size(); i++) {
            if (this.shuttles.get(i).getOrigin().getId().equals(terminal.getId())){
                temp.add(this.shuttles.get(i));
            }
        }
        return temp;
    }

    @Override
    public Path findShortestPath(TerminalBase origin, TerminalBase destination) {
        PriorityQueue<PathNode> queue = new PriorityQueue<>(new PathNode(0));
        HashMap<String, PathNode> queueTracker = new HashMap<String, PathNode>();

        for (int j = 0; j < this.terminals.size(); j++){
            if (origin.getId().equals(this.terminals.get(j).getId())) {
                PathNode temp = new PathNode(this.terminals.get(j), 0, 0);
                queueTracker.put(temp.terminal.getId(), temp);
                queue.add(temp);
            } else {
                PathNode temp1 = new PathNode(this.terminals.get(j), -1, -1);
                queueTracker.put(temp1.terminal.getId(), temp1);
                queue.add(temp1);
            }
        }
        Path path = new Path(new ArrayList<TerminalBase>(), 0);



        while (!queue.isEmpty()) {
            PathNode min = queue.poll();
            List<ShuttleBase> shuttles = this.outgoingShuttles(min.terminal);
            for (int i = 0; i < shuttles.size(); i++) {
                TerminalBase des = shuttles.get(i).getDestination();
                int newTime = 0;
                int count = 0;
                if (!des.getId().equals(destination.getId())) {
                    newTime = shuttles.get(i).getTime() + des.getWaitingTime() + min.value;
                } else {
                    newTime = shuttles.get(i).getTime() + min.value;
                }
                count = min.pathCount + 1;
                PathNode desNode = queueTracker.get(des.getId());

                if ((desNode.pathCount > count || desNode.pathCount == -1) && min.t.contains(origin)) {
                    desNode.t = new ArrayList<>();
                    desNode.t.addAll(min.t);
                    desNode.t.add(desNode.terminal);
                    desNode.pathCount = count;
                    desNode.value = newTime;
                    List<ShuttleBase> tempShuttles = new ArrayList<>(min.shuttles);
                    tempShuttles.add(shuttles.get(i));
                    desNode.shuttles = new ArrayList<ShuttleBase>(tempShuttles);
                }
            }
        }
        PathNode lastNode = queueTracker.get(destination.getId());
        if (lastNode.pathCount == -1) {
            return null;
        } else {
            path.time = lastNode.pathCount ;
            path.terminals = lastNode.t;
            for (int k = 0; k < lastNode.shuttles.size(); k++) {
                Shuttle tem = (Shuttle) lastNode.shuttles.get(k);
                tem.capacity -= 1;
                if (tem.capacity == 0) {
                    this.shuttles.remove(tem);
                }
            }
        }
        return path;
    }

    @Override
    public Path findFastestPath(TerminalBase origin, TerminalBase destination) {
        PriorityQueue<TimeNode> queue = new PriorityQueue<>(new TimeNode(0));
        HashMap<String, TimeNode> queueTracker = new HashMap<String, TimeNode>();

        for (int j = 0; j < this.terminals.size(); j++){
            if (origin.getId().equals(this.terminals.get(j).getId())) {
                TimeNode temp = new TimeNode(this.terminals.get(j), 0);
                queueTracker.put(temp.terminal.getId(), temp);
                queue.add(temp);
            } else {
                TimeNode temp1 = new TimeNode(this.terminals.get(j), -1);
                queueTracker.put(temp1.terminal.getId(), temp1);
                queue.add(temp1);
            }
        }
        Path path = new Path(new ArrayList<TerminalBase>(), 0);
        if (!origin.getId().equals(destination.getId())) {
            path.time += origin.getWaitingTime();
        }


        while (!queue.isEmpty()) {
            TimeNode min = queue.poll();
            List<ShuttleBase> shuttles = this.outgoingShuttles(min.terminal);
            for (int i = 0; i < shuttles.size(); i++) {
                TerminalBase des = shuttles.get(i).getDestination();
                int newTime = 0;
                if (!des.getId().equals(destination.getId())) {
                    newTime = shuttles.get(i).getTime() + des.getWaitingTime() + min.value;
                } else {
                    newTime = shuttles.get(i).getTime() + min.value;
                }

                TimeNode desNode = queueTracker.get(des.getId());


                if ((desNode.value > newTime || desNode.value == -1) && min.t.contains(origin)){
                    desNode.t = new ArrayList<>();
                    desNode.t.addAll(min.t);
                    desNode.t.add(desNode.terminal);
                    desNode.value = newTime;
                    List<ShuttleBase> tempShuttles = new ArrayList<>(min.shuttles);
                    tempShuttles.add(shuttles.get(i));
                    desNode.shuttles = new ArrayList<ShuttleBase>(tempShuttles);
                }
            }
        }
        TimeNode lastNode = queueTracker.get(destination.getId());
        if (lastNode.value == -1) {
            return null;
        } else {
            path.time += lastNode.value;
            path.terminals = lastNode.t;
            for (int k = 0; k < lastNode.shuttles.size(); k++) {
                Shuttle tem = (Shuttle) lastNode.shuttles.get(k);
                tem.capacity -= 1;
                if (tem.capacity == 0) {
                    this.shuttles.remove(tem);
                }
            }

        }
        return path;

    }

    /* Implement all the necessary methods of the Airport here */

    static class Terminal extends TerminalBase {

        public Terminal(String id, int waitingTime) {
            super(id, waitingTime);
        }

        /* Implement all the necessary methods of the Terminal here */
    }

    static class Shuttle extends ShuttleBase {

        public int capacity;

        public Shuttle(TerminalBase origin, TerminalBase destination, int time) {
            super(origin, destination, time);
        }

        /* Implement all the necessary methods of the Shuttle here */
    }

    /*
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        REMOVE THE MAIN FUNCTION BEFORE SUBMITTING TO THE AUTOGRADER
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        The following main function is provided for simple debugging only

        Note: to enable assertions, you need to add the "-ea" flag to the
        VM options of Airport's run configuration
     */
    

}

class TimeNode implements Comparator<TimeNode> {
    AirportBase.TerminalBase terminal;
    int value;
    List<AirportBase.ShuttleBase> shuttles = new ArrayList<>();
    List<AirportBase.TerminalBase> t = new ArrayList<>();

    public TimeNode(int i) {

    }

    public TimeNode(AirportBase.TerminalBase tt, int v) {
        this.terminal = tt;
        this.value = v;
        this.t.add(this.terminal);
    }

    @Override
    public int compare(TimeNode n1, TimeNode n2) {
        if (n1.value >= n2.value && n1.value != -1 && n2.value != -1) {
            return 1;
        }
        else if (n1.value == n2.value) {
            if (n1.terminal.getId().compareTo(n2.terminal.getId()) >= 0) {
                return 1;
            }
        }
        else if (n1.value == -1) {
            return 1;
        }
        return -1;
    }
}

class PathNode implements Comparator<PathNode> {
    AirportBase.TerminalBase terminal;
    int value;
    int pathCount;
    List<AirportBase.ShuttleBase> shuttles = new ArrayList<>();
    List<AirportBase.TerminalBase> t = new ArrayList<>();

    public PathNode(int i) {

    }

    public PathNode(AirportBase.TerminalBase tt, int v, int p) {
        this.terminal = tt;
        this.value = v;
        this.pathCount = p;
        this.t.add(this.terminal);
    }

    public int compare(PathNode n1, PathNode n2) {
        if (n1.pathCount >= n2.pathCount && n1.pathCount != -1 && n2.pathCount != -1) {
            return 1;
        } else if (n1.pathCount == n2.pathCount) {
            if (n1.terminal.getId().compareTo(n2.terminal.getId()) >= 0) {
                return 1;
            }
        } else if (n1.pathCount == -1) {
            return 1;
        }
        return -1;
    }

}

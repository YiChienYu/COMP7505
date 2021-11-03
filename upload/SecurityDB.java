public class SecurityDB extends SecurityDBBase {

    private int sizeOfHashTable;
    private Record[] records;

    public SecurityDB(int numPlanes, int numPassengersPerPlane){
        super(numPlanes, numPassengersPerPlane);
        this.sizeOfHashTable = Prime.nextPrime(numPlanes, numPassengersPerPlane);
        if (sizeOfHashTable > 1021) {
            sizeOfHashTable = 1021;
        }
        this.records = new Record[sizeOfHashTable];

    }

    public int calculateHashCode(String key){
        int size = key.length();
        int[] arr = new int[size];
        int temp = 0;
        int result = 0;
        for (int i = 0; i < size; i++){
            temp += (int) key.charAt(i);
            arr[i] = (temp+1);
        }

        for (int j = 0; j < size; j++){
            result += arr[j];
        }
        return result;
    }

    public int compression(int n){
        return n % sizeOfHashTable;
    }

    public int size(){
        return this.sizeOfHashTable;
    }

    public String get(String passportId){
        int p = calculateHashCode(passportId);
        int position = compression(p);
        int start = position;
        //System.out.println(start);

        while (true){

            Record result = this.records[position];
            if (result != null){
                //System.out.println(1);
                if (result.password.equals(passportId)) {
                    return result.name;
                }
            }
            //System.out.println(2);
            if ((start == 0 && position == (this.sizeOfHashTable -1)) || position == (start-1)) {
                return null;
            }

            position = (position+1)%sizeOfHashTable;
        }
    }

    public boolean remove(String passportId){
        int position = calculateHashCode(passportId);
        position = compression(position);
        int start = position;
        while (true){
            Record result = this.records[position];
            if (result != null){
                if (result.password.equals(passportId)) {
                    this.records[position] = null;
                    return true;
                }
            }
            if ((start == 0 && position == this.sizeOfHashTable -1) || position == (start-1)) {
                return false;
            }

            position = (position+1)%sizeOfHashTable;


        }
    }

    public boolean addPassenger(String name, String passportId){
        String checkName = this.get(passportId);
        //System.out.println(checkName);
        int position = calculateHashCode(passportId);
        position = compression(position);
        int start = position;
        if (checkName != null){
            if (!(checkName.equals(name))) {
                System.err.println("Suspicious behaviour");
                return false;
            } else {
                while (true){
                    Record result = this.records[position];
                    if (result.name.equals(name)){
                        if (result.times >= 5){
                            System.err.println("Suspicious behaviour");
                            return false;
                        }
                        result.times+=1;
                        return true;
                    }
                    position = (position+1)%sizeOfHashTable;
                }
            }
        } else {
            while (true){
                Record result = this.records[position];
                if (result == null){
                    Record r = new Record(name, passportId);
                    this.records[position] = r;
                    r.times+=1;
                    return true;
                }
                if ((start == 0 && position == this.sizeOfHashTable -1) || position == (start-1)) {
                    return false;
                }
                position = (position+1)%sizeOfHashTable;


            }
        }

    }

    public int count(){
        int count = 0;
        for (int i = 0; i < records.length; i++){
            if (records[i] != null){
                count += 1;
            }
        }
        return count;
    }

    public int getIndex(String passportId){
        int position = calculateHashCode(passportId);
        position = compression(position);
        while (true){
            Record result = this.records[position];
            if (result == null || result.password.equals(passportId)){
                return position;
            }
            position = (position+1)%sizeOfHashTable;

        }
    }

    /* Implement all the necessary methods here */

    /*
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        REMOVE THE MAIN FUNCTION BEFORE SUBMITTING TO THE AUTOGRADER
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        The following main function is provided for simple debugging only

        Note: to enable assertions, you need to add the "-ea" flag to the
        VM options of SecurityDB's run configuration
     */

}

/* Add any additional helper classes here */
class Record {
    public String password;
    public String name;
    public int times;
    public Record(String n, String p){
        times = 0;
        password = p;
        name = n;
    }
}
class Prime {
    public static int nextPrime(int n, int k){
        int temp = n*k;
        int ans = 1;
        for (int i = temp+1; i < 2*temp; i++){
            for (int j =2; j <= i/2;j++){
                if(i%j==0){
                    break;
                } else if (j==i/2){
                    ans=i;
                    return ans;
                }
            }
        }
        return ans;
    }
}

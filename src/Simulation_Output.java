
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.PriorityQueue;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author TIAN MAOKUN
 */
public class Simulation_Output {

    public static final int NUM_ITERATION = 10000;
    public static final int NUM_STATION = 20;
    public static final int NUM_CHANNEL = 10;
    public static final int STATION_LENGTH = 2;
    public static final int WARM_UP_PERIOD = 2500;
    public static int NUM_FINISHED_CALL = 10000;
    public static int CALL_INPUT_GENERATE = 15000;
    public static Call[] calls;
    public static int[] stations;
    public static double clock;
    public static PriorityQueue<CallEvent> eventQueue;
    public static int dropCallCounter;
    public static int blockCallCounter;
    public static int finishedCallCounter;
    public static int warmUpCounter;

    public static void generateInput() {
        calls = new Call[CALL_INPUT_GENERATE];

        int i;
        for (i = 0; i < calls.length; i++) {
            calls[i] = new Call(i);
        }
    }

    public static void initialize() {

        stations = new int[NUM_STATION];
        int i;

        for (i = 0; i < NUM_STATION; i++) {
            stations[i] = 0;
        }

        clock = 0;

        eventQueue = new PriorityQueue<>(CALL_INPUT_GENERATE, new Comparator<CallEvent>() {
            @Override
            public int compare(CallEvent event1, CallEvent event2) {
                // head of the priority queue is the least element with
                // respect to the specified ordering
                if (event1.time < event2.time) {
                    return -1;
                } else if (event1.time > event2.time) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        dropCallCounter = 0;
        blockCallCounter = 0;

        finishedCallCounter = 0;
        warmUpCounter = 0;

        double currentTime = 0;
        for (i = 0; i < calls.length; i++) {
            currentTime = currentTime + calls[i].interArrivalTime;
            eventQueue.add(new CallEvent(calls[i].id, calls[i].baseStation, currentTime, calls[i].callDuration, "Initialization"));
        }
    }

    public static void simulate(int numChannelReserved) {
        initialize();

        CallEvent event;

        while (warmUpCounter < WARM_UP_PERIOD) {
            event = eventQueue.poll();
            clock = event.time;
            if (event.type.equals("Initialization")) {
                if (stations[event.station - 1] >= (NUM_CHANNEL - numChannelReserved)) {
                    warmUpCounter++;
                } else {
                    stations[event.station - 1]++;
                    if (calls[event.callID].distanceToEnd / calls[event.callID].velocity < event.duration) {
                        eventQueue.add(new CallEvent(event.callID, event.station, clock + calls[event.callID].distanceToEnd / calls[event.callID].velocity, event.duration - (calls[event.callID].distanceToEnd / calls[event.callID].velocity), "Handover"));
                    } else {
                        eventQueue.add(new CallEvent(event.callID, event.station, clock + event.duration, 0, "Termination"));
                    }
                }
            } else if (event.type.equals("Termination")) {
                stations[event.station - 1]--;
                warmUpCounter++;
            } else if (event.type.contains("Handover")) {
                stations[event.station - 1]--;
                if (event.station == NUM_STATION) {
                    warmUpCounter++;
                } else {
                    if (stations[event.station] == NUM_CHANNEL) {
                        warmUpCounter++;
                    } else {
                        stations[event.station]++;
                        if (STATION_LENGTH / calls[event.callID].velocity < event.duration) {
                            eventQueue.add(new CallEvent(event.callID, event.station + 1, clock + STATION_LENGTH / calls[event.callID].velocity, event.duration - (STATION_LENGTH / calls[event.callID].velocity), "Handover"));
                        } else {
                            eventQueue.add(new CallEvent(event.callID, event.station + 1, clock + event.duration, 0, "Termination"));
                        }
                    }
                }
            }
        }

        while (finishedCallCounter < NUM_FINISHED_CALL) {
            event = eventQueue.poll();
            clock = event.time;
            if (event.type.equals("Initialization")) {
                if (stations[event.station - 1] >= (NUM_CHANNEL - numChannelReserved)) {
                    blockCallCounter++;
                    finishedCallCounter++;
                } else {
                    stations[event.station - 1]++;
                    if (calls[event.callID].distanceToEnd / calls[event.callID].velocity < event.duration) {
                        eventQueue.add(new CallEvent(event.callID, event.station, clock + calls[event.callID].distanceToEnd / calls[event.callID].velocity, event.duration - (calls[event.callID].distanceToEnd / calls[event.callID].velocity), "Handover"));
                    } else {
                        eventQueue.add(new CallEvent(event.callID, event.station, clock + event.duration, 0, "Termination"));
                    }
                }
            } else if (event.type.equals("Termination")) {
                stations[event.station - 1]--;
                finishedCallCounter++;
            } else if (event.type.contains("Handover")) {
                stations[event.station - 1]--;
                if (event.station == NUM_STATION) {
                    finishedCallCounter++;
                } else {
                    if (stations[event.station] == NUM_CHANNEL) {
                        dropCallCounter++;
                        finishedCallCounter++;
                    } else {
                        stations[event.station]++;
                        if (STATION_LENGTH / calls[event.callID].velocity < event.duration) {
                            eventQueue.add(new CallEvent(event.callID, event.station + 1, clock + STATION_LENGTH / calls[event.callID].velocity, event.duration - (STATION_LENGTH / calls[event.callID].velocity), "Handover"));
                        } else {
                            eventQueue.add(new CallEvent(event.callID, event.station + 1, clock + event.duration, 0, "Termination"));
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        int i, j;

        int MAX_NUM_RESERVE = 1;

        Files.createFile(Paths.get("output5.csv"));
        PrintWriter writer = new PrintWriter("output5.csv");
        
        writer.println(NUM_FINISHED_CALL);
        writer.println("No.,Channel Reserved,Dropped Call,Blocked Call,Channel Reserved,Dropped Call,Blocked Call");
        for (i = 0; i < NUM_ITERATION; i++) {
            writer.print(i);
            generateInput();
            for (j = 0; j <= MAX_NUM_RESERVE; j++) {
                simulate(j);
                writer.print("," + j + "," + dropCallCounter + "," + blockCallCounter);
            }
            writer.println("");
            writer.flush();
        }

        writer.flush();
        writer.close();
    }
}

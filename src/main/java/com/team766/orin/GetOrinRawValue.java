package com.team766.orin;

import edu.wpi.first.networktables.DoubleArrayEntry;
import edu.wpi.first.networktables.DoubleArrayTopic;
import edu.wpi.first.networktables.IntegerArrayEntry;
import edu.wpi.first.networktables.IntegerArrayTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/*
 * This class is used to get raw values from the NetworkTable. We can send values on the Orin to NetworkTables and get them easily here.
 * One important caveat of the NetworkTables code is that it doesn't throw exceptions when a value is not found.
 * Instead, it asks for a default value to return. This is a problem because we can't know if the value is not found or if the value is the default value.
 * Thus, numbers were chosen as default values that aren't really plausible to be sent by the Orin.
 * For example, it is not likley that the Orin will send Integer.MIN_VALUE as a value. In this case, an exception is thrown.
 *
 * @author Max Spier, 9/28/2024
 */
public class GetOrinRawValue {

    private static NetworkTableInstance inst = NetworkTableInstance.getDefault();
    private static NetworkTable table = inst.getTable("/SmartDashboard");

    private static DoubleArrayTopic pose = table.getDoubleArrayTopic("raw_pose");
    private static double[] arr = new double[] {Double.NEGATIVE_INFINITY};
    private static DoubleArrayEntry poseValues = pose.getEntry(arr);

    private static IntegerArrayTopic tagId = table.getIntegerArrayTopic("tag_id");
    private static long[] array1 = new long[] {0l};
    private static IntegerArrayEntry tagIdValues = tagId.getEntry(array1);

    public static double[] getRawPoseData() throws ValueNotFoundOnTableError {
        if (poseValues.get().length == 1 && poseValues.get()[0] == Double.NEGATIVE_INFINITY) {
            throw new ValueNotFoundOnTableError("Pose Data Not Present on Table");
        }

        return poseValues.get();
    }

    public static int[] getTagIds() throws ValueNotFoundOnTableError {
        long[] longValues = tagIdValues.get();

        if (longValues.length == 0
                || (longValues[0] == Integer.MIN_VALUE && longValues.length == 1)) {
            throw new ValueNotFoundOnTableError("Tag ID Data Not Present on Table");
        }

        int[] toReturn = new int[longValues.length];
        for (int i = 0; i < longValues.length; i++) {
            toReturn[i] = (int) longValues[i];
        }
        return toReturn;
    }

    public static void closeAll() {
        poseValues.close();
        tagIdValues.close();
    }
}

// Placeholder methods - can iterate on these as needed
/*
public static int getInt(String key) throws ValueNotFoundOnTableError {
    IntegerTopic topic = table.getIntegerTopic(key);
    IntegerEntry value = topic.getEntry(Integer.MIN_VALUE);
    if (value.get() == Integer.MIN_VALUE) {
        throw new ValueNotFoundOnTableError(key);
    }
    return (int) value.get();
}

public static double getDouble(String key) throws ValueNotFoundOnTableError {
    DoubleTopic topic = table.getDoubleTopic(key);
    DoubleEntry value = topic.getEntry(Double.NEGATIVE_INFINITY);
    if (value.get() == Double.NEGATIVE_INFINITY) {
        throw new ValueNotFoundOnTableError(key);
    }
    return value.get();
}


//Be careful to know that the boolean value is valid. If the value is not found, the default value is returned and no exception is thrown.
//This is because true/false are the only two possible values for a boolean - if one were to be the default it would be hard to know if the value was not found or if the value was the default.

public static boolean getBoolean(String key) {
    BooleanTopic topic = table.getBooleanTopic(key);
    BooleanEntry value = topic.getEntry(false);
    return value.get();
}

public static String getString(String key) throws ValueNotFoundOnTableError {
    StringTopic topic = table.getStringTopic(key);
    StringEntry value =
            topic.getEntry(
                    "Team 766 is such a great team and they will go to worlds every year because they are such a great team!!!");
    if (value.get()
            .equals(
                    "Team 766 is such a great team and they will go to worlds every year because they are such a great team!!!")) {
        throw new ValueNotFoundOnTableError(key);
    }
    return value.get();
}

public static int[] getIntArray(String key) throws ValueNotFoundOnTableError {
    IntegerArrayTopic topic = table.getIntegerArrayTopic(key);
    long[] arr = new long[1];
    arr[0] = Integer.MIN_VALUE;
    IntegerArrayEntry values = topic.getEntry(arr);
    long[] array = values.get();

    if (array.length == 0 || (array[0] == Integer.MIN_VALUE && array.length == 1)) {
        throw new ValueNotFoundOnTableError(key);
    }

    int[] toReturn = new int[array.length];
    for (int i = 0; i < array.length; i++) {
        toReturn[i] = (int) array[i];
    }
    return toReturn;
}

public static double[] getDoubleArray(String key) throws ValueNotFoundOnTableError {
    DoubleArrayTopic topic = table.getDoubleArrayTopic(key);
    double[] arr = new double[] {Double.NEGATIVE_INFINITY};
    DoubleArrayEntry values = topic.getEntry(arr);
    if (values.get().length == 1 && values.get()[0] == Double.NEGATIVE_INFINITY) {
        throw new ValueNotFoundOnTableError(key);
    }

    return values.get();
}
*/

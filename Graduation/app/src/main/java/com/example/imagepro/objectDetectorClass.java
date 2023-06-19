package com.example.imagepro;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.checkerframework.checker.units.qual.A;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class  objectDetectorClass {
    // should start from small letter

    // this is used to load model and predict
    private Interpreter interpreter;
    // store all label in array
    private Interpreter signsInterpreter;
    private List<String> labelList;
    private int INPUT_SIZE;
    private int PIXEL_SIZE=3; // for RGB
    private int IMAGE_MEAN=0;
    private  float IMAGE_STD=255.0f;
    private int imageSize =0;
    // use to initialize gpu in app
    private GpuDelegate gpuDelegate;
    private int height=0;
    private  int width=0;

    objectDetectorClass(AssetManager assetManager,String modelPath, String labelPath,int inputSize,String signsModel,int imageSize) throws IOException{
        INPUT_SIZE=inputSize;
        // use to define gpu or cpu // no. of threads
        this.imageSize =imageSize;
        Interpreter.Options options=new Interpreter.Options();
        gpuDelegate=new GpuDelegate();
        options.addDelegate(gpuDelegate);
        options.setNumThreads(4); // set it according to your phone
        // loading model
        interpreter=new Interpreter(loadModelFile(assetManager,modelPath),options);
        // load labelmap
        labelList=loadLabelList(assetManager,labelPath);

        Interpreter.Options options2 =new Interpreter.Options();

        options2.setNumThreads(2);
        signsInterpreter = new Interpreter(loadModelFile(assetManager,signsModel),options2);


    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        // to store label
        List<String> labelList=new ArrayList<>();
        // create a new reader
        BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        // loop through each line and store it to labelList
        while ((line=reader.readLine())!=null){
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        // use to get description of file
        AssetFileDescriptor fileDescriptor=assetManager.openFd(modelPath);
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset =fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }
    // create new Mat function
    public Mat recognizeImage(Mat mat_image){
        try {

            // Rotate original image by 90 degree get get portrait frame

            // This change was done in video: Does Your App Keep Crashing? | Watch This Video For Solution.
            // This will fix crashing problem of the app

            Mat rotated_mat_image = new Mat();

            Mat a = mat_image.t();
            Core.flip(a, rotated_mat_image, 1);
            // Release mat
            a.release();

            // if you do not do this process you will get improper prediction, less no. of object
            // now convert it to bitmap
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(rotated_mat_image.cols(), rotated_mat_image.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rotated_mat_image, bitmap);
            // define height and width
            height = bitmap.getHeight();
            width = bitmap.getWidth();

            // scale the bitmap to input size of model
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

            // convert bitmap to bytebuffer as model input should be in it
            ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);

            // defining output
            // 10: top 10 object detected
            // 4: there coordinate in image
            //  float[][][]result=new float[1][10][4];
            Object[] input = new Object[1];
            input[0] = byteBuffer;

            Map<Integer, Object> output_map = new TreeMap<>();
            // we are not going to use this method of output
            // instead we create treemap of three array (boxes,score,classes)

            float[][][] boxes = new float[1][10][4];
            // 10: top 10 object detected
            // 4: there coordinate in image
            float[][] scores = new float[1][10];
            // stores scores of 10 object
            float[][] classes = new float[1][10];
            // stores class of object

            // add it to object_map;
            output_map.put(0, boxes);
            output_map.put(1, classes);
            output_map.put(2, scores);

            // now predict
            interpreter.runForMultipleInputsOutputs(input, output_map);
            // Before watching this video please watch my previous 2 video of
            //      1. Loading tensorflow lite model
            //      2. Predicting object
            // In this video we will draw boxes and label it with it's name

            Object value = output_map.get(0);
            Object Object_class = output_map.get(1);
            Object score = output_map.get(2);

            // loop through each object
            // as output has only 10 boxes
            for (int i = 0; i < 10; i++) {
                float class_value = (float) Array.get(Array.get(Object_class, 0), i);
                float score_value = (float) Array.get(Array.get(score, 0), i);
                // define threshold for score

                // Here you can change threshold according to your model
                // Now we will do some change to improve app
                if (score_value > 0.5) {
                    Object box1 = Array.get(Array.get(value, 0), i);
                    // we are multiplying it with Original height and width of frame

                    float y1 = (float) Array.get(box1, 0) * height;
                    float x1 = (float) Array.get(box1, 1) * width;
                    float y2 = (float) Array.get(box1, 2) * height;
                    float x2 = (float) Array.get(box1, 3) * width;
                    if (y1 < 0) {
                        y1 = 0;
                    }
                    if (x1 < 0) {
                        x1 = 0;
                    }
                    if (y2 > height) {
                        y1 = height;
                    }
                    if (x2 > width) {
                        y1 = width;
                    }

                    float w1 = x2 - x1;
                    float h1 = y2 - y1;

                    Rect cropped_img = new Rect((int) x1, (int) y2, (int) w1, (int) h1);
                    Mat cropped = new Mat(rotated_mat_image, cropped_img).clone();
                    Bitmap bitmap1 = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(cropped, bitmap1);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap1, imageSize, imageSize, false);
                    ByteBuffer byteBuffer1 = convertBitmapToByteBuffer1(resizedBitmap);
                    float[][] outputArray = new float[1][29];
                    signsInterpreter.run(byteBuffer1, outputArray);
                    float output = getMax(outputArray);
                    Log.d("ObjectDetectionClass", "output_class_value: " + output);
                    String predictedSign = getSymbol(output);
                    Imgproc.putText(rotated_mat_image, predictedSign, new Point(x1 + 20, y1 + 40), 2, 1.5, new Scalar(255, 255, 255, 255), 2);
                    // draw rectangle in Original frame //  starting point    // ending point of box  // color of box       thickness
                    Imgproc.rectangle(rotated_mat_image, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 255, 0, 255), 2);
                    // write text on frame
                    // string of class name of object  // starting point                         // color of text           // size of text
                }

            }
            Mat b = rotated_mat_image.t();
            Core.flip(b, mat_image, 0);
            b.release();
            // Now for second change go to CameraBridgeViewBase
            return mat_image;
        }
        catch (Exception e){
            return null;
        }
    }

    private float getMax(float[][] outputArray) {
        float maxNumber = Float.MIN_VALUE; // Initialize with the smallest possible value
        int maxColumnIndex = -1;
            for (int j = 0; j < outputArray[0].length; j++) {
                if (outputArray[0][j] > maxNumber) {
                    maxNumber = outputArray[0][j];
                    maxColumnIndex = j;
                }
            }
        return maxColumnIndex;
    }

    private String getSymbol(float value) {
        if (value ==0){
            return "A";
        }else if (value ==1){
            return "B";
        }else if (value ==2){
            return "C";
        }else if (value ==3){
            return "D";
        }else if (value ==4){
            return "E";
        }else if (value ==5){
            return "F";
        }else if (value ==6){
            return "G";
        }else if (value ==7){
            return "H";
        }else if (value ==8){
            return "I";
        }else if (value ==9){
            return "J";
        }else if (value ==10){
            return "K";
        }else if (value ==11){
            return "L";
        }else if (value ==12){
            return "M";
        }else if (value ==13){
            return "N";
        }else if (value ==14){
            return "O";
        }else if (value ==15){
            return "P";
        }else if (value ==16){
            return "Q";
        }else if (value ==17){
            return "R";
        }else if (value ==18){
            return "S";
        }else if (value ==19){
            return "T";
        }else if (value ==20){
            return "U";
        }else if (value ==21){
            return "V";
        }else if (value ==22){
            return "W";
        }else if (value ==23){
            return "X";
        }else if (value ==24){
            return "Y";
        }else if (value ==25){
            return "Z";
        }else if (value ==26){
            return "Space";
        }else if (value ==27){
            return "Del";
        }else {
            return "Nothing";
        }
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        int quant=1;
        int size_images=INPUT_SIZE;
        if(quant==0){
            byteBuffer=ByteBuffer.allocateDirect(1*size_images*size_images*3);
        }
        else {
            byteBuffer=ByteBuffer.allocateDirect(4*1*size_images*size_images*3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[size_images*size_images];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel=0;

        // some error
        //now run
        for (int i=0;i<size_images;++i){
            for (int j=0;j<size_images;++j){
                final  int val=intValues[pixel++];
                if(quant==0){
                    byteBuffer.put((byte) ((val>>16)&0xFF));
                    byteBuffer.put((byte) ((val>>8)&0xFF));
                    byteBuffer.put((byte) (val&0xFF));
                }
                else {
                    // paste this
                    byteBuffer.putFloat((((val >> 16) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val >> 8) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val) & 0xFF))/255.0f);
                }
            }
        }
        return byteBuffer;
    }
    private ByteBuffer convertBitmapToByteBuffer1(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        int quant = 1;
        int size_images = INPUT_SIZE;
        if (quant == 0) {
            byteBuffer = ByteBuffer.allocateDirect(1 * size_images * size_images * 3);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(4 * 1 * size_images * size_images * 3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[size_images * size_images];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;

        // Perform normalization
        for (int i = 0; i < size_images; ++i) {
            for (int j = 0; j < size_images; ++j) {
                final int val = intValues[pixel++];
                float normalizedR = ((val >> 16) & 0xFF) / 255.0f;
                float normalizedG = ((val >> 8) & 0xFF) / 255.0f;
                float normalizedB = (val & 0xFF) / 255.0f;

                if (quant == 0) {
                    byteBuffer.put((byte) (normalizedR * 255));
                    byteBuffer.put((byte) (normalizedG * 255));
                    byteBuffer.put((byte) (normalizedB * 255));
                } else {
                    byteBuffer.putFloat(normalizedR);
                    byteBuffer.putFloat(normalizedG);
                    byteBuffer.putFloat(normalizedB);
                }
            }
        }
        return byteBuffer;
    }

}
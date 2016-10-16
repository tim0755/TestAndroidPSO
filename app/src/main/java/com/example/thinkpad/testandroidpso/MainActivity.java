package com.example.thinkpad.testandroidpso;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.org.gandhim.pso.PSOConstants;
import com.org.gandhim.pso.PSOProcess;
import com.org.gandhim.pso.Point;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private PSOProcess mPSOProcess;

    private static final int MAX_POINT = 29;
    private static final double[] matchPoint1X = {-379.7635943455036, -372.93515895979846, -373.47468270076575, -377.18079093617007, -375.05911208915063, -365.40836648023907, -381.54299645534087, -327.58166331301334, -390.5275247325558, -379.1143791736382, -370.69758965370943, -378.71024256724974, -375.25547559616905, -370.8535472131012, -379.3660457999261, -375.3242730490851, -361.0488542713016, -342.73833781506283, -340.246834786334, -342.87531987114147, -334.00340330498335, -370.36444469355814, -369.97530822871556, -372.56806618105685, -368.22721872827714, -359.02234386355815, -354.42049840815866, -346.3253160452441, -335.2649662009891};
    private static final double[] matchPoint1Y = {-9.30392860214536, 1.003094108736939, 7.5611339907610216, 20.9781692010191, 43.89183363679408, 45.77013128099774, 50.7707006322679, 84.03318970429257, -4.094074151110514, 28.605663808115587, 45.61644356864675, -39.358879345701915, -33.2394567107549, -29.992075289776633, 28.738034724938522, 44.26409584521723, 83.57745430547051, 102.8138180201795, 111.85252560621987, 119.60581204713696, 131.5273061389309, -29.378932858847694, 7.626004022522503, 19.81967082692241, 38.47251486111892, 45.82464646182489, 77.81768795453621, 131.1264437379427, 132.2032135664861};
    private static final double[] matchPoint2X = {357.7895868410465, 365.63675262238814, 363.41186985900237, 358.2243197424325, 356.00709320349125, 364.73736645977937, 348.15647830355726, 382.27273536518527, 346.36709824360327, 355.4608103141683, 360.8762445232085, 357.38053795664666, 362.2814767719189, 367.2242645623785, 355.59512465036363, 356.54486873960474, 356.08371667936393, 361.65613320104103, 357.3017104018659, 350.71770292083045, 349.4373375463682, 368.16103793925663, 368.29339048451743, 363.92404694904536, 365.018676619816, 372.260162505114, 363.680118710449, 340.6169814023214, 349.4373375463682};
    private static final double[] matchPoint2Y = {-11.572594223752045, -4.099836667300165, 3.6500455041039546, 16.176314226743234, 36.22317060412076, 41.53326282358123, 39.82912526640044, 91.97507637426123, -8.416230080058764, 22.355064669196928, 39.036537468044095, -40.15858186774575, -35.22158690464613, -32.44742415120825, 22.325861202927843, 35.95469619314902, 76.30985158260015, 100.83156859354312, 112.09468548124993, 114.82980674567489, 129.88718463512632, -31.826603514073167, 2.4657557420721252, 14.405777061223777, 33.03558480734299, 42.19113188608141, 75.52978482560859, 120.67764141991584, 129.88718463512632};

    Point[] points1 = new Point[MAX_POINT];
    Point[] points2 = new Point[MAX_POINT];

    private boolean mRunning = false;
    private double[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Executors.newFixedThreadPool(1).execute(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    if (!mRunning) {
                        //System.currentTimeMillis();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }
                    mRunning = false;
                    result = mPSOProcess.execute();
                    int i = 0;
                    System.out.print("result:(");
                    for (i = 0; i < PSOConstants.PROBLEM_DIMENSION; i++) {
                        System.out.print("" + result[i] + " , ");
                    }
                    System.out.print(" " + (result[i - 1] - result[i - 2]));
                    System.out.println(")");
                }
            }
        });

        for (int i = 0; i < MAX_POINT; i++) {
            points1[i] = new Point(matchPoint1X[i], matchPoint1Y[i]);
            points2[i] = new Point(matchPoint2X[i], matchPoint2Y[i]);
        }
        mPSOProcess = new PSOProcess(points1, points2);
        mRunning = true;
    }
}

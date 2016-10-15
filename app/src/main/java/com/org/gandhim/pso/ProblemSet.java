package com.org.gandhim.pso;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is the problem to be solved
// to find an x and a y that minimize the function below:
// f(x, y) = (2.8125 - x + x * y^4)^2 + (2.25 - x + x * y^2)^2 + (1.5 - x + x*y)^2
// where 1 <= x <= 4, and -1 <= y <= 1

// you can modify the function depends on your needs
// if your problem space is greater than 2-dimensional space
// you need to introduce a new variable (other than x and y)

public class ProblemSet {
    public static final double LOC_X_LOW = -10;//azimuth 方位角
    public static final double LOC_X_HIGH = 10;
    public static final double LOC_Y_LOW = -10;//elevation 仰角
    public static final double LOC_Y_HIGH = 10;
    public static final double LOC_Z_LOW = -10;//rotate 旋转角
    public static final double LOC_Z_HIGH = 10;
    public static final double LOC_X_LOW2 = -10;//azimuth 方位角
    public static final double LOC_X_HIGH2 = 10;
    public static final double LOC_Y_LOW2 = -10;//elevation 仰角
    public static final double LOC_Y_HIGH2 = 10;
    public static final double LOC_Z_LOW2 = -10;//rotate 旋转角
    public static final double LOC_Z_HIGH2 = 10;
    public static final double VEL_LOW = -1;
    public static final double VEL_HIGH = 1;

    public static final double ERR_TOLERANCE = 10;//1E-20; // the smaller the tolerance, the more accurate the result,
    // but the number of iteration is increased

    private static final double M_PI = Math.PI;
    private static final int TARGET_WIDTH = 1920;
    private static final int TARGET_HEIGHT = 960;
    public static final double optimalPixelSize = 0.003691;//0.00375
    private static final int POLYNOME_FITTING_LEVEL_F = 9;//(8+1)
    private static final double coeff_f[/*POLYNOME_FITTING_LEVEL_F*/] = {
            0.0,
            1.058926970695910,
            0.110779815783390,
            -0.649682084041335,
            2.013709296704290,
            -3.227184832623470,
            2.875136560968330,
            -1.336509669684520,
            0.255559958265590
    };

    private Point[] mMatchPointFishEye1;
    private Point[] mMatchPointFishEye2;

    private double minTotal = 10000000.0;

    private double pLongitude, pLatitude;

    private Point[][] mTargetPoint;

    public ProblemSet(Point[] points1, Point[] points2) {
        mMatchPointFishEye1 = new Point[points1.length];
        mMatchPointFishEye2 = new Point[points1.length];
        System.arraycopy(points1, 0, mMatchPointFishEye1, 0, points1.length);
        System.arraycopy(points2, 0, mMatchPointFishEye2, 0, points2.length);
        System.out.println("ProblemSet mMatchPointFishEye1:" + mMatchPointFishEye1.length + "   mMatchPointFishEye2:" + mMatchPointFishEye2.length);
        mTargetPoint = new Point[points1.length][2];

        for (int i = 0; i < mMatchPointFishEye1.length; i++) {
            mTargetPoint[i][0] = new Point(0, 0);
            mTargetPoint[i][1] = new Point(0, 0);
        }
    }

    private void sphereRotation_f(double longitude, double latitude, double camAzimuth, double camElevation/*, double *pNewLongitude, double *pNewLatitude */) {
        double deltaElevation;
        double deltaAzimuth;

        deltaElevation = M_PI / 2.0 - camElevation;
        deltaAzimuth = longitude;

        pLatitude = Math.asin(Math.sin(latitude) * Math.cos(deltaElevation) - Math.cos(latitude) * Math.cos(deltaAzimuth) * Math.sin(deltaElevation));
        pLongitude = Math.atan2(Math.cos(latitude) * Math.sin(deltaAzimuth), Math.cos(latitude) * Math.cos(deltaAzimuth) * Math.cos(deltaElevation) + Math.sin(latitude) * Math.sin(deltaElevation)) + camAzimuth;

        if (pLongitude > M_PI)
            pLongitude -= 2.0 * M_PI;

        if (pLongitude < -M_PI)
            pLongitude += 2.0 * M_PI;
    }

    // This function calculate the fishEye to longitude-latitude projection, using pre-determined polygon fitting
    private boolean fishEye2Sphere(double x, double y, double camAzimuth, double camElevation/*, double *pLongitude, double *pLatitude */, double pixelSize) {

        double polygon;
        double distance;
        double latitude;
        double longitude;

        distance = Math.sqrt(x * x + y * y) * pixelSize/*pixelSize_f*/;
        latitude = 0.0;
        polygon = 1.0;

        for (int i = 0; i < POLYNOME_FITTING_LEVEL_F; i++) {
            latitude += polygon * coeff_f[i];
            polygon *= distance;
        }

        if (latitude > M_PI) {
            System.out.println("fishEye2Sphere false");
            return false;
        }

        latitude = M_PI / 2.0 - latitude;
        longitude = Math.atan2(x, y);
        sphereRotation_f(longitude, latitude, camAzimuth, camElevation/*, pLongitude, pLatitude*/);
        return true;
    }

    private double fishEye2EquirecProjectionForward(double[] azimuthDelta, double[] elevationDelta, double[] rotateDelta, double pixelSize) {
        double x1, y1, x2, y2;
        double xDelta1, yDelta1, xDelta2, yDelta2;
        double xTarget1, yTarget1, xTarget2, yTarget2;
        double cosRotate1, sinRotate1;
        double cosRotate2, sinRotate2;
        double total = 0;
        double distance;

        cosRotate1 = Math.cos(-rotateDelta[0]);
        sinRotate1 = Math.sin(-rotateDelta[0]);
        cosRotate2 = Math.cos(-rotateDelta[1]);
        sinRotate2 = Math.sin(-rotateDelta[1]);
        //LogWrap.d("fishEye2EquirecProjectionForward " + mMatchPointFishEye1.length);
        for (int i = 0; i < mMatchPointFishEye1.length; i++) {
            x1 = mMatchPointFishEye1[i].x;
            y1 = mMatchPointFishEye1[i].y;
            xDelta1 = cosRotate1 * x1 + sinRotate1 * y1;
            yDelta1 = -sinRotate1 * x1 + cosRotate1 * y1;
            if (!fishEye2Sphere(xDelta1, yDelta1, azimuthDelta[0], elevationDelta[0], pixelSize))
                continue;
            xTarget1 = (pLongitude + M_PI) / (2.0 * M_PI) * TARGET_WIDTH;
            yTarget1 = (-pLatitude + M_PI / 2.0) / M_PI * TARGET_HEIGHT;

            x2 = mMatchPointFishEye2[i].x;
            y2 = mMatchPointFishEye2[i].y;
            xDelta2 = cosRotate2 * x2 + sinRotate2 * y2;
            yDelta2 = -sinRotate2 * x2 + cosRotate2 * y2;
            if (!fishEye2Sphere(xDelta2, yDelta2, azimuthDelta[1], elevationDelta[1], pixelSize))
                continue;
            xTarget2 = (pLongitude + M_PI) / (2.0 * M_PI) * TARGET_WIDTH;
            yTarget2 = (-pLatitude + M_PI / 2.0) / M_PI * TARGET_HEIGHT;

            distance = Math.pow(xTarget1 - xTarget2, 2.0) + Math.pow(yTarget1 - yTarget2, 2.0);
            total += distance;

            mTargetPoint[i][0].x = xTarget1;
            mTargetPoint[i][0].y = yTarget1;
            mTargetPoint[i][1].x = xTarget2;
            mTargetPoint[i][1].y = yTarget2;

            if (total > minTotal) {
                return total;
            }
        }
        return total;
    }

    public double evaluate(Location location) {
        double x1 = location.getLoc()[0] * Math.PI / 180; // the "x" part of the location
        double y1 = location.getLoc()[1] * Math.PI / 180; // the "y" part of the location
        double z1 = location.getLoc()[2] * Math.PI / 180; // the "z" part of the location
        double x2 = location.getLoc()[3] * Math.PI / 180; // the "x" part of the location
        double y2 = location.getLoc()[4] * Math.PI / 180; // the "y" part of the location
        double z2 = location.getLoc()[5] * Math.PI / 180; // the "z" part of the location
        double[] azimuth = {x1, -Math.PI - x2};
        double[] elevation = {y1, -y2};
        double[] rotate = {z1, -z2};

        return fishEye2EquirecProjectionForward(azimuth, elevation, rotate, optimalPixelSize);
    }

    public void show() {
        for (int i = 0; i < mMatchPointFishEye1.length; i++) {
            System.out.println("(x1,x2):(y1,y2):(dx,dy)  (" + mTargetPoint[i][0].x + "," + mTargetPoint[i][1].x + "):(" + mTargetPoint[i][0].y + "," + mTargetPoint[i][1].y + ") (" + (mTargetPoint[i][0].x-mTargetPoint[i][1].x) + "," + (mTargetPoint[i][0].y-mTargetPoint[i][1].y) + ")");
        }
    }
}

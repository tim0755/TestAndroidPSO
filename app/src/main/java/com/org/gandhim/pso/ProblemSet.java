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

    public static final double ERR_TOLERANCE = 10;//1E-20; // the smaller the tolerance, the more accurate the result,
    // but the number of iteration is increased

    private static final double M_PI = Math.PI;
    private static final int TARGET_WIDTH = 1920;
    private static final int TARGET_HEIGHT = 960;
    private static final double optimalPixelSize = 0.003691;//0.00375
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

    private static final double MIN_DIMENSION_RANGE0 = -5;
    private static final double MAX_DIMENSION_RANGE0 = 5;
    private static final double MIN_DIMENSION_RANGE1 = -5;
    private static final double MAX_DIMENSION_RANGE1 = 5;
    private static final double MIN_DIMENSION_RANGE2 = -5;
    private static final double MAX_DIMENSION_RANGE2 = 5;
    private static final double MIN_DIMENSION_RANGE3 = 0.0035;
    private static final double MAX_DIMENSION_RANGE3 = 0.0038;
    private static final double MIN_DIMENSION_RANGE4 = -5;
    private static final double MAX_DIMENSION_RANGE4 = 5;
    private static final double MIN_DIMENSION_RANGE5 = -5;
    private static final double MAX_DIMENSION_RANGE5 = 5;
    private static final double MIN_DIMENSION_RANGE6 = -5;
    private static final double MAX_DIMENSION_RANGE6 = 5;
    private static final double MIN_DIMENSION_RANGE7 = 0.0035;
    private static final double MAX_DIMENSION_RANGE7 = 0.0038;
    public double[][] mDimensionRange;

    public static final double VEL_LOW = -0.5;
    public static final double VEL_HIGH = 0.5;
    public static final double VEL_LOW2 = -0.00005;
    public static final double VEL_HIGH2 = 0.00005;
    public double[][] mDimensionVelocity;

    private double minTotal = 10000000.0;

    private double pLongitude, pLatitude;

    private Point[][] mTargetPoint;

    public ProblemSet(Point[] points1, Point[] points2) {
        mMatchPointFishEye1 = new Point[points1.length];
        mMatchPointFishEye2 = new Point[points1.length];
        System.arraycopy(points1, 0, mMatchPointFishEye1, 0, points1.length);
        System.arraycopy(points2, 0, mMatchPointFishEye2, 0, points2.length);
        //System.out.println("ProblemSet mMatchPointFishEye1:" + mMatchPointFishEye1.length + "   mMatchPointFishEye2:" + mMatchPointFishEye2.length);
        mTargetPoint = new Point[points1.length][2];

        for (int i = 0; i < mMatchPointFishEye1.length; i++) {
            mTargetPoint[i][0] = new Point(0, 0);
            mTargetPoint[i][1] = new Point(0, 0);
        }

        mDimensionRange = new double[PSOConstants.PROBLEM_DIMENSION][2];
        mDimensionRange[0][0] = MIN_DIMENSION_RANGE0;
        mDimensionRange[0][1] = MAX_DIMENSION_RANGE0;
        mDimensionRange[1][0] = MIN_DIMENSION_RANGE1;
        mDimensionRange[1][1] = MAX_DIMENSION_RANGE1;
        mDimensionRange[2][0] = MIN_DIMENSION_RANGE2;
        mDimensionRange[2][1] = MAX_DIMENSION_RANGE2;
        mDimensionRange[3][0] = MIN_DIMENSION_RANGE3;
        mDimensionRange[3][1] = MAX_DIMENSION_RANGE3;

        mDimensionRange[4][0] = MIN_DIMENSION_RANGE4;
        mDimensionRange[4][1] = MAX_DIMENSION_RANGE4;
        mDimensionRange[5][0] = MIN_DIMENSION_RANGE5;
        mDimensionRange[5][1] = MAX_DIMENSION_RANGE5;
        mDimensionRange[6][0] = MIN_DIMENSION_RANGE6;
        mDimensionRange[6][1] = MAX_DIMENSION_RANGE6;
        mDimensionRange[7][0] = MIN_DIMENSION_RANGE7;
        mDimensionRange[7][1] = MAX_DIMENSION_RANGE7;

        mDimensionVelocity = new double[PSOConstants.PROBLEM_DIMENSION][2];
        mDimensionVelocity[0][0] = VEL_LOW;
        mDimensionVelocity[0][1] = VEL_HIGH;
        mDimensionVelocity[1][0] = VEL_LOW;
        mDimensionVelocity[1][1] = VEL_HIGH;
        mDimensionVelocity[2][0] = VEL_LOW;
        mDimensionVelocity[2][1] = VEL_HIGH;
        mDimensionVelocity[3][0] = VEL_LOW2;
        mDimensionVelocity[3][1] = VEL_HIGH2;

        mDimensionVelocity[4][0] = VEL_LOW;
        mDimensionVelocity[4][1] = VEL_HIGH;
        mDimensionVelocity[5][0] = VEL_LOW;
        mDimensionVelocity[5][1] = VEL_HIGH;
        mDimensionVelocity[6][0] = VEL_LOW;
        mDimensionVelocity[6][1] = VEL_HIGH;
        mDimensionVelocity[7][0] = VEL_LOW2;
        mDimensionVelocity[7][1] = VEL_HIGH2;
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

    private double fishEye2EquirecProjectionForward(double[] azimuthDelta, double[] elevationDelta, double[] rotateDelta, double[] pixelSize) {
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
            if (!fishEye2Sphere(xDelta1, yDelta1, azimuthDelta[0], elevationDelta[0], pixelSize[0]))
                continue;
            xTarget1 = (pLongitude + M_PI) / (2.0 * M_PI) * TARGET_WIDTH;
            yTarget1 = (-pLatitude + M_PI / 2.0) / M_PI * TARGET_HEIGHT;

            x2 = mMatchPointFishEye2[i].x;
            y2 = mMatchPointFishEye2[i].y;
            xDelta2 = cosRotate2 * x2 + sinRotate2 * y2;
            yDelta2 = -sinRotate2 * x2 + cosRotate2 * y2;
            if (!fishEye2Sphere(xDelta2, yDelta2, azimuthDelta[1], elevationDelta[1], pixelSize[1]))
                continue;
            xTarget2 = (pLongitude + M_PI) / (2.0 * M_PI) * TARGET_WIDTH;
            yTarget2 = (-pLatitude + M_PI / 2.0) / M_PI * TARGET_HEIGHT;

            distance = Math.pow(Math.pow(xTarget1 - xTarget2, 2.0) + Math.pow(yTarget1 - yTarget2, 2.0), 1.0 / 2);
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
        double x0 = location.getLoc()[0] * Math.PI / 180;
        double y0 = location.getLoc()[1] * Math.PI / 180;
        double z0 = location.getLoc()[2] * Math.PI / 180;
        double p0 = location.getLoc()[3];

        double x1 = location.getLoc()[4] * Math.PI / 180;
        double y1 = location.getLoc()[5] * Math.PI / 180;
        double z1 = location.getLoc()[6] * Math.PI / 180;
        double p1 = location.getLoc()[7];

        double[] azimuth = {x0, -Math.PI - x1};
        double[] elevation = {y0, -y1};
        double[] rotate = {z0, -z1};
        double[] pixelSize = {p0, p1};

        minTotal = 10000000.0;

        return fishEye2EquirecProjectionForward(azimuth, elevation, rotate, pixelSize);
    }

    public void show() {
        for (int i = 0; i < mMatchPointFishEye1.length; i++) {
            System.out.println("(x1,x2):(y1,y2):(dx,dy)  (" + mTargetPoint[i][0].x + " , " + mTargetPoint[i][1].x + ") : (" + mTargetPoint[i][0].y + " , " + mTargetPoint[i][1].y + ") (" + (mTargetPoint[i][0].x - mTargetPoint[i][1].x) + " , " + (mTargetPoint[i][0].y - mTargetPoint[i][1].y) + ")");
        }
    }
}

package com.mlieber.KanjiKing.Search;

import android.util.Log;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by nudge on 05.11.16.
 *
 * Uses code available at ftp.monash.edu.au for stroke to kanji (handwriting) recognition,
 * heavily refactored for readability and ease of understanding.
 */
public class DrawResult {
    private static final String TAG = "DrawResult";

    public static final int RATING_SIZE = 10;

    private String[] top_rated;
    private Vector xstrokes, ystrokes;

    static final int angScale = 1000;
    static final int sCost = (int)Math.round(Math.PI/60.0*angScale);
    static final int hugeCost = ((int)Math.round(Math.PI*angScale)+sCost)*100;

    public DrawResult(Vector xstrokes, Vector ystrokes) {
        top_rated = new String[RATING_SIZE];
        this.xstrokes = xstrokes;
        this.ystrokes = ystrokes;
        analyze();
    }

    public String[] getTopRated() {
        return top_rated;
    }

    private void analyze() {
        clear();

        if ((xstrokes == null) || (ystrokes == null)) {
            Log.v(TAG, "xstrokes or ystrokes null! Giving up analysis...");
            return;
        }

        if (xstrokes.size() != ystrokes.size()) {
            Log.v(TAG, "xstrokes and ystrokes size differ! Giving up analysis...");
            return;
        }

        if ((xstrokes.size() == 0) || (xstrokes.size() > Strokes.STROKES.length)) {
            Log.v(TAG, "xstrokes.size "+xstrokes.size()+" out of STROKES range (0-"+Strokes.STROKES.length+"). Giving up analysis...");
            return;
        }

        findTopRated();
    }

    private void findTopRated() {
        TopScore topScore = new TopScore(RATING_SIZE);
        Stroke stroke = new Stroke("");

        // Iterate over all available stroke entries
        for (int liner = 0; liner < Strokes.STROKES[xstrokes.size() -1].length; liner++) {
            String line = Strokes.STROKES[xstrokes.size() -1][liner];
            stroke.fill(line);
            String goline = getGoLine(stroke.strokes);

            // Calculate the score for this possible kanji
            int score;
            if (topScore.size() < RATING_SIZE) {
                score = getScore(goline, 999999);
            } else {
                score = getScore(goline,Math.min(topScore.getBestScore(), topScore.getWorstScore() * 2));
            }

            // if more tokens exist, apply filters
            if (stroke.args != null) {
                score += getArgScore(stroke.args);
            }

            topScore.add(stroke.kanji, score);
        }

        // copy the values
        top_rated = topScore.getValues();
    }

    private int getScore(String s, int cutoff) {
        double score = 0;
        int strokes = 0;
        int maxscore = 0;

        StringTokenizer st = new StringTokenizer(s);
        Enumeration xe = xstrokes.elements();
        Enumeration ye = ystrokes.elements();

        // Calculate the score of each stroke
        while (st.hasMoreTokens()) {
            if (!xe.hasMoreElements()) {
                return (99997);
            } else {
                Vector vxe = (Vector) xe.nextElement();
                Vector vye = (Vector) ye.nextElement();
                int thisscore = getStrokeScore(
                        vxe,
                        vye,
                        0,
                        vxe.size(),
                        st.nextToken(),
                        0
                );

                score = score + thisscore * thisscore;
                maxscore = Math.max(maxscore, thisscore);
                strokes++;
            }
        }

        if (xe.hasMoreElements()) {
            return(99998);
        }

        if (strokes == 0) {
            return (99997);
        }

        return ((int) Math.round(Math.sqrt(score)));
    }

    private int getArgScore(String args) {
        StringTokenizer st = new StringTokenizer(args);
        while (st.hasMoreTokens())
        {
            try
            {
                String tok = st.nextToken();
                int minindex;
                minindex = tok.indexOf("-");
                if (minindex==-1)
                {
                    System.out.println("bad filter");
                    continue;
                }
                String arg1, arg2;
                arg1 = tok.substring(0,minindex);
                arg2 = tok.substring(minindex+1,tok.length());
                int arg1stroke, arg2stroke;
                arg1stroke = Integer.parseInt(arg1.substring(1));
                boolean must=(arg2.charAt(arg2.length()-1)=='!');
                if (must) arg2stroke=Integer.parseInt(arg2.substring(1,arg2.length()-1));
                else arg2stroke = Integer.parseInt(arg2.substring(1));
                Vector stroke1x, stroke1y, stroke2x, stroke2y;
                stroke1x = (Vector)xstrokes.elementAt(arg1stroke-1);
                stroke1y = (Vector)ystrokes.elementAt(arg1stroke-1);
                stroke2x = (Vector)xstrokes.elementAt(arg2stroke-1);
                stroke2y = (Vector)ystrokes.elementAt(arg2stroke-1);

                int val1, val2;
                switch (arg1.charAt(0))
                {
                    case 'x':  val1 = ((Integer)stroke1x.firstElement()).intValue(); break;
                    case 'y':  val1 = ((Integer)stroke1y.firstElement()).intValue(); break;
                    case 'i':  val1 = ((Integer)stroke1x.lastElement()).intValue(); break;
                    case 'j':  val1 = ((Integer)stroke1y.lastElement()).intValue(); break;
                    case 'a':  val1 = (((Integer)stroke1x.firstElement()).intValue()+
                            ((Integer)stroke1x.lastElement()).intValue())/2; break;
                    case 'b': val1 = (((Integer)stroke1y.firstElement()).intValue()+
                            ((Integer)stroke1y.lastElement()).intValue())/2; break;
                    case 'l': int dx, dy;
                        dx = ((Integer)stroke1x.lastElement()).intValue()-
                                ((Integer)stroke1x.firstElement()).intValue();
                        dy = ((Integer)stroke1y.lastElement()).intValue()-
                                ((Integer)stroke1y.firstElement()).intValue();
                        val1 = (int)(Math.sqrt((double)(dx*dx + dy*dy)));
                        break;
                    default:
                        System.out.println("bad filter");
                        continue;
                }
                // now the same thing for arg2 & val2
                switch (arg2.charAt(0))
                {
                    case 'x': val2 = ((Integer)stroke2x.firstElement()).intValue(); break;
                    case 'y': val2 = ((Integer)stroke2y.firstElement()).intValue(); break;
                    case 'i': val2 = ((Integer)stroke2x.lastElement()).intValue(); break;
                    case 'j': val2 = ((Integer)stroke2y.lastElement()).intValue(); break;
                    case 'a': val2 = (((Integer)stroke2x.firstElement()).intValue()+
                            ((Integer)stroke2x.lastElement()).intValue())/2; break;
                    case 'b': val2 = (((Integer)stroke2y.firstElement()).intValue()+
                            ((Integer)stroke2y.lastElement()).intValue())/2;
                        break;
                    case 'l': int dx, dy;
                        dx = ((Integer)stroke2x.lastElement()).intValue()-
                                ((Integer)stroke2x.firstElement()).intValue();
                        dy = ((Integer)stroke2y.lastElement()).intValue()-
                                ((Integer)stroke2y.firstElement()).intValue();
                        val2 = (int)(Math.sqrt((double)(dx*dx + dy*dy))); break;
                    default: System.out.println("bad filter");
                        continue;
                }
                // so now val1 and val2 have the right values
                if (must && (val1<val2)) {
                    return 9999999;
                }
                return -(val1-val2);
            }
            catch (Exception ez2)
            {
                System.out.println("bad filter");
                continue;
            } // try-catch
        } // while

        return 0;
    }

    /**
     * Calculate the score of one single stroke
     * Whatever that comment meant: endi is exclusive, begi inclusive
     */
    private int getStrokeScore(
            Vector xv,
            Vector yv,
            int begi,
            int endi,
            String dir,
            int depth) {

        if (dir.length()==1) {
            int difx, dify;
            difx = ((Integer)xv.elementAt(endi-1)).intValue() -
                    ((Integer)xv.elementAt(begi)).intValue();
            dify = ((Integer)yv.elementAt(endi-1)).intValue() -
                    ((Integer)yv.elementAt(begi)).intValue();
            if ((difx == 0) && (dify == 0)) {
                return hugeCost;
            }

            if ((difx*difx+dify*dify>(20*20))&&(endi-begi>5)&&(depth<4)) {
                int mi = (endi+begi) / 2;
                int cost1,cost2;
                cost1=getStrokeScore(xv,yv,begi,mi,dir,depth+1);
                cost2=getStrokeScore(xv,yv,mi,endi,dir,depth+1);
                // return the average cost of the substrokes, but penalize if they're different.
                return (cost1+cost2) / 2;
            }

            double ang;
            ang = Math.atan2(-dify, difx);
            double myang = calculateAngle(dir.charAt(0));
            double difang = getAngleDifference(myang, ang);

            return (int) Math.round(difang * angScale) + sCost;
        } else if (begi == endi) {
            return hugeCost * dir.length();
        } else {
            // recurse
            int l1, l2;
            l1 = dir.length() / 2;
            l2 = dir.length() - l1;
            String s1, s2;
            s1 = dir.substring(0, l1);
            s2 = dir.substring(l1, dir.length());

            int mincost = hugeCost * dir.length() * 2;
            int s1l = s1.length();
            int s2l = s2.length();
            int step = (endi-begi)/10;
            if (step<1) step=1;

            for (int i = begi + 1 + s1l; i < endi - 1 - s2l; i += step) {
                int ncost;
                ncost = getStrokeScore(xv, yv, begi, i+1, s1, depth)+
                        getStrokeScore(xv, yv, i-1, endi, s2, depth);
                if (ncost < mincost) {
                    mincost = ncost;
                }
            }

            return mincost;
        }
    }

    private double getAngleDifference(double myAngle, double angle) {
        double diff = myAngle - angle;
        return normalizeAngle(diff);
    }

    /**
     * Normalizes the angle so that it is always positive and between 0 and PI.
     * Also, the angle is "normalized" in a way that the smaller direction (of a crossing) is used.
     *
     * @param angle
     * @return angle
     */
    private double normalizeAngle(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }

        while (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }

        if (angle > Math.PI) {
            angle = 2 * Math.PI - angle;
        }

        return angle;
    }

    private double calculateAngle(char c) {
        switch (c) {
            case '6': return 0.0d;
            case '9': return Math.PI/4;
            case '8': return Math.PI/2;
            case '7': return Math.PI*3/4;
            case '4': return Math.PI;
            case '3': return -Math.PI/4;
            case '2': return -Math.PI/2;
            case '1': return -Math.PI*3/4;
            default:
                // System.out.println("Illegal char!");
                return 0.0d;
        }
    }

    private String getGoLine(String strokes) {
        String goline = "";
        StringTokenizer st = new StringTokenizer(strokes);

        if (st.countTokens() != xstrokes.size()) {
            Log.v(TAG, "st.countTokens() differ from xstrokes.size()! in line: "+ strokes);
            return null;
        }

        while (st.hasMoreTokens())
        {
            String tok = st.nextToken();
            for (int i=0; i < tok.length(); i++)
            {
                switch (tok.charAt(i))
                {
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '6':
                    case '7':
                    case '8':
                    case '9': goline=goline+tok.charAt(i); break;
                    case 'b': goline=goline+"62"; break;
                    case 'c': goline=goline+"26"; break;
                    case 'x': goline=goline+"21"; break;
                    case 'y': goline=goline+"23"; break;
                    case '|': return goline;
                    default:
                        System.out.println("Unknown symbol "+tok.charAt(i)+" in strokes string: "+strokes);
                        break;
                }
            }
            goline=goline+" ";
        }

        return goline;
    }

    private void clear() {
        top_rated = new String[] {"", "", "", "", "", "", "", "", "", "" };
    }
}

package com.mlieber.KanjiKing.Element;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Vector;

/**
 * Created by nudge on 04.11.16.
 */
public class DrawPanel extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint  paint;

    private Vector xstrokes = new Vector();
    private Vector ystrokes = new Vector();
    private Vector curxvec = null;
    private Vector curyvec = null;
    private int lastx, lasty;

    private DrawListener drawListener;

    public DrawPanel(Context context) {
        super(context);
        setup();
    }

    public DrawPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup();
    }

    public void clear() {
        xstrokes.removeAllElements();
        ystrokes.removeAllElements();
        curxvec = null;
        curyvec = null;
        createGraphics(this.getWidth(), this.getHeight());
        invalidate();
    }

    public void setDrawListener(DrawListener drawListener) {
        this.drawListener = drawListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createGraphics(w, h);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
    }

    private void setup() {
        createGraphics(240, 360);

        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // Pressed Event
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (curxvec != null) {
                        // Was: Redraw all previous strokes with another color
                        // Do we need this again?
                    }

                    curxvec = new Vector();
                    curyvec = new Vector();
                    xstrokes.addElement(curxvec);
                    ystrokes.addElement(curyvec);
                    lastx = Math.round(motionEvent.getX());
                    lasty = Math.round(motionEvent.getY());
                    curxvec.addElement(new Integer(lastx));
                    curyvec.addElement(new Integer(lasty));

                    return true;
                }

                // Unpressed event
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (drawListener != null) {
                        drawListener.onStrokeFinish(xstrokes, ystrokes);
                    }
                    return true;
                }

                // Dragged Event
                int x, y;
                x = Math.round(motionEvent.getX());
                y = Math.round(motionEvent.getY());
                curxvec.addElement(new Integer(x));
                curyvec.addElement(new Integer(y));
                canvas.drawLine((float) lastx, (float) lasty, (float) x, (float) y, paint);
                lastx = x;
                lasty = y;
                invalidate();

                return true;
            }
        });
    }

    private void createGraphics(int w, int h) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.STROKE);
    }
}
